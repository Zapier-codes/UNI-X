package com.unix.app.ui.screens.payments

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unix.app.data.academic.TuitionInvoice
import com.unix.app.data.payments.CurrencyLocator
import com.unix.app.data.payments.FxRates
import com.unix.app.data.payments.PaymentRepository
import com.unix.app.data.payments.PaymentResult
import com.unix.app.data.payments.SupportedCurrency
import com.unix.app.ui.screens.components.ScreenTitle
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Strictly checkout: the app never collects card numbers, OTPs, or bank
 * details. It asks the university's backend for a checkout_url and hands
 * the browser off to Korapay's own hosted page (via Custom Tabs) for
 * everything payment-related.
 */
@Composable
fun CheckoutScreen(
    invoice: TuitionInvoice,
    studentName: String,
    studentEmail: String,
    paymentRepository: PaymentRepository,
    onPaymentReference: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currency by remember { mutableStateOf(SupportedCurrency.USD) }
    var displayAmount by remember { mutableStateOf(invoice.baseAmountUsd) }
    var loadingCheckout by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val guess = CurrencyLocator.fromDeviceLocale(Locale.getDefault())
        currency = paymentRepository.detectCurrency(fallback = guess)
        displayAmount = FxRates.convertFromUsd(invoice.baseAmountUsd, currency)
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle("Tuition Checkout", "${invoice.sessionLabel} · ${invoice.semesterLabel}")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(invoice.programme, style = MaterialTheme.typography.titleMedium)
                Text(invoice.dueLabel, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${currency.symbol}${"%,.2f".format(displayAmount)} ${currency.code}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    "Priced from \$${"%,.2f".format(invoice.baseAmountUsd)} USD base tuition, shown in your local currency based on your location. You'll be charged in ${currency.code} on the next screen.",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            enabled = !loadingCheckout,
            onClick = {
                loadingCheckout = true
                errorMessage = null
                scope.launch {
                    val result = paymentRepository.startCheckout(
                        studentName = studentName,
                        studentEmail = studentEmail,
                        amount = displayAmount,
                        currency = currency,
                        narration = "${invoice.sessionLabel} ${invoice.semesterLabel} tuition — ${invoice.programme}",
                    )
                    loadingCheckout = false
                    when (result) {
                        is PaymentResult.CheckoutReady -> {
                            onPaymentReference(result.reference)
                            val intent = CustomTabsIntent.Builder().build()
                            intent.launchUrl(context, Uri.parse(result.checkoutUrl))
                        }
                        is PaymentResult.Failed -> errorMessage = result.message
                        else -> Unit
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (loadingCheckout) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text("Pay with Korapay")
        }

        Text(
            "You'll be securely redirected to Korapay's checkout page. UNI X never sees your card or bank details.",
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
