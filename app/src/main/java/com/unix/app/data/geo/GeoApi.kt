package com.unix.app.data.geo

import retrofit2.http.GET

/**
 * https://ipapi.co — free tier genuinely requires no signup, no API key,
 * and no credit card (unlike ipgeolocation.io, which despite being "free"
 * still requires registering for a key). 1,000 requests/day, HTTPS
 * supported on the free tier, currency included directly in the response.
 *
 * Used purely for "what currency should we show this visitor" — it is
 * never involved in the actual payment, which stays entirely with the
 * university's own backend (see data/payments/PaymentApi.kt) and Korapay.
 */
interface GeoApi {
    @GET("json/")
    suspend fun lookup(): IpApiCoResponse
}
