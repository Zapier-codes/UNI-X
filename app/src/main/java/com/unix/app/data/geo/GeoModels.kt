package com.unix.app.data.geo

import com.google.gson.annotations.SerializedName

data class IpGeoResponse(
    val ip: String? = null,
    val location: IpGeoLocation? = null,
    val currency: IpGeoCurrency? = null,
)

data class IpGeoLocation(
    @SerializedName("country_code2") val countryCode2: String? = null,
    @SerializedName("country_name") val countryName: String? = null,
    val city: String? = null,
)

data class IpGeoCurrency(
    val code: String? = null,
    val name: String? = null,
    val symbol: String? = null,
)
