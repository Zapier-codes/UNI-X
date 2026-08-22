package com.unix.app.data.geo

import com.google.gson.annotations.SerializedName

/** Matches ipapi.co's /json/ response shape (only the fields UNI X uses;
 *  the real response includes more — city, timezone, org, etc.). */
data class IpApiCoResponse(
    val ip: String? = null,
    @SerializedName("country_code") val countryCode: String? = null,
    @SerializedName("country_name") val countryName: String? = null,
    val currency: String? = null,
    @SerializedName("currency_name") val currencyName: String? = null,
    // ipapi.co returns an "error": true + "reason" field on failed lookups
    // (e.g. rate limit hit) instead of an HTTP error code — check this
    // before trusting the fields above.
    val error: Boolean? = null,
    val reason: String? = null,
)
