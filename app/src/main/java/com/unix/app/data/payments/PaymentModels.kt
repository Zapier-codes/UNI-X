package com.unix.app.data.payments

import com.google.gson.annotations.SerializedName

data class CustomerInfo(
    val name: String,
    val email: String,
)

data class InitializePaymentRequest(
    val amount: Double,
    val currency: String,
    val reference: String,
    val customer: CustomerInfo,
    // Where Korapay sends the browser after payment completes/fails.
    @SerializedName("redirect_url") val redirectUrl: String = "unix://payment-redirect",
    // Free-text description shown on the gateway, e.g. "Fall 2026 - Semester 1 tuition".
    val narration: String? = null,
)

data class InitializePaymentResponse(
    val status: Boolean? = null,
    val message: String? = null,
    val data: InitializePaymentData? = null,
)

data class InitializePaymentData(
    val reference: String,
    @SerializedName("checkout_url") val checkoutUrl: String,
)

data class VerifyPaymentResponse(
    val status: Boolean? = null,
    val message: String? = null,
    val data: VerifyPaymentData? = null,
)

data class VerifyPaymentData(
    val reference: String,
    val status: String, // expected: "success" | "processing" | "failed"
    val amount: Double? = null,
    val currency: String? = null,
)
