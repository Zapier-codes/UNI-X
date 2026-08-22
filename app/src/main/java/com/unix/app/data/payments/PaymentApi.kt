package com.unix.app.data.payments

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Client for the university's own hosted payment backend
 * (https://b-pay-backend.onrender.com), NOT Korapay directly. Korapay's
 * secret key must only ever live server-side — this backend was built
 * against Korapay's Checkout Redirect flow:
 *   1. POST /api/pay  -> backend calls Korapay's
 *      POST /merchant/api/v1/charges/initialize and returns a checkout_url
 *   2. The app opens checkout_url in a Custom Tab
 *   3. Korapay redirects back to our app (unix://payment-redirect?reference=...)
 *   4. GET /api/verify/{reference} -> backend calls Korapay's verify-charge
 *      endpoint and returns the confirmed status. ALWAYS trust this
 *      server-verified status over anything read from the redirect URL.
 *
 * Field names below follow Korapay's own initialize/verify conventions
 * since the backend was built from their docs. If the live backend uses
 * different field names, only this file + [PaymentModels] need to change.
 *
 * Geolocation/currency detection does NOT go through this backend — see
 * data/geo/GeoApi.kt (ipapi.co) for that.
 */
interface PaymentApi {

    @POST("api/pay")
    suspend fun initializePayment(@Body request: InitializePaymentRequest): InitializePaymentResponse

    @GET("api/verify/{reference}")
    suspend fun verifyPayment(@Path("reference") reference: String): VerifyPaymentResponse

    @GET("health")
    suspend fun health(): Map<String, Any>
}
