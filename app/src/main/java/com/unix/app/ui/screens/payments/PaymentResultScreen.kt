package com.unix.app.ui.screens.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unix.app.data.payments.PaymentRepository
import com.unix.app.data.payments.PaymentResult

@Composable
fun PaymentResultScreen(
    reference: String,
    paymentRepository: PaymentRepository,
    onDone: () -> Unit,
) {
    var status by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var checking by remember { mutableStateOf(true) }

    LaunchedEffect(reference) {
        // The redirect URL's own query params are informational only — the
        // number that matters is what the backend confirms server-side.
        when (val result = paymentRepository.verify(reference)) {
            is PaymentResult.Verified -> status = result.status
            is PaymentResult.Failed -> error = result.message
            else -> Unit
        }
        checking = false
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        if (checking) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                "Confirming your payment with the university...",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
            return@Column
        }

        when {
            status == "success" -> {
                Text("Payment confirmed", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Your tuition payment has been received and verified. Reference: $reference",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            status == "processing" -> {
                Text("Payment processing", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Your payment is still being confirmed by Korapay. This can take a few minutes — check back on this invoice shortly.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            status != null -> {
                Text("Payment not completed", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Status: $status. If you completed payment on Korapay's page, this will update shortly.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            else -> {
                Text("Couldn't confirm payment", style = MaterialTheme.typography.headlineMedium)
                Text(
                    error ?: "Please check your connection and try again from the invoice screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        Button(onClick = onDone, modifier = Modifier.padding(top = 24.dp)) {
            Text("Done")
        }
    }
}
