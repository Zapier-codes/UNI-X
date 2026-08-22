package com.unix.app.data.geo

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * https://ipgeolocation.io — the standard, widely-used IP geolocation
 * service (free tier: 1,000 lookups/day, no credit card). Used purely for
 * "what currency should we show this visitor" — it is never involved in
 * the actual payment, which stays entirely with the university's own
 * backend (see data/payments/PaymentApi.kt) and Korapay.
 */
interface GeoApi {
    @GET("v3/ipgeo")
    suspend fun lookup(
        @Query("apiKey") apiKey: String,
        // Trim the response to just what we use, per ipgeolocation.io's
        // own "fields" parameter for keeping requests light.
        @Query("fields") fields: String = "location,currency",
    ): IpGeoResponse
}
