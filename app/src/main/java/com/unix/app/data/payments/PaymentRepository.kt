package com.unix.app.data.payments

import com.unix.app.data.geo.GeoApi
import java.util.UUID

sealed class PaymentResult {
    data class CheckoutReady(val checkoutUrl: String, val reference: String) : PaymentResult()
    data class Verified(val status: String, val reference: String) : PaymentResult()
    data class Failed(val message: String) : PaymentResult()
}

/**
 * Strictly-checkout flow: this app never touches card data, OTPs, or a
 * Korapay secret key directly. It only ever asks the backend to (a) create
 * a checkout session and (b) confirm what actually happened, and trusts
 * the browser-hosted Korapay checkout page for everything in between.
 *
 * Currency detection is a separate concern, deliberately not routed
 * through the payment backend — see [GeoApi] (ipapi.co).
 */
class PaymentRepository(
    private val api: PaymentApi,
    private val geoApi: GeoApi,
) {

    suspend fun detectCurrency(fallback: SupportedCurrency): SupportedCurrency {
        return try {
            val geo = geoApi.lookup()
            CurrencyLocator.fromGeoLookup(geo)
        } catch (e: Exception) {
            fallback
        }
    }

    suspend fun startCheckout(
        studentName: String,
        studentEmail: String,
        amount: Double,
        currency: SupportedCurrency,
        narration: String,
    ): PaymentResult {
        val reference = "UNIX-${UUID.randomUUID().toString().take(12)}"
        return try {
            val response = api.initializePayment(
                InitializePaymentRequest(
                    amount = amount,
                    currency = currency.code,
                    reference = reference,
                    customer = CustomerInfo(name = studentName, email = studentEmail),
                    narration = narration,
                ),
            )
            val data = response.data
            if (response.status != false && data != null) {
                PaymentResult.CheckoutReady(checkoutUrl = data.checkoutUrl, reference = data.reference)
            } else {
                PaymentResult.Failed(response.message ?: "Could not start checkout. Please try again.")
            }
        } catch (e: Exception) {
            PaymentResult.Failed(e.message ?: "Network error starting checkout.")
        }
    }

    /** ALWAYS the source of truth for whether a student's tuition is paid —
     *  never trust the redirect URL's own query params for that, only that
     *  it tells us which reference to re-verify server-side. */
    suspend fun verify(reference: String): PaymentResult {
        return try {
            val response = api.verifyPayment(reference)
            val data = response.data
            if (data != null) {
                PaymentResult.Verified(status = data.status, reference = data.reference)
            } else {
                PaymentResult.Failed(response.message ?: "Could not verify payment status.")
            }
        } catch (e: Exception) {
            PaymentResult.Failed(e.message ?: "Network error verifying payment.")
        }
    }
}
