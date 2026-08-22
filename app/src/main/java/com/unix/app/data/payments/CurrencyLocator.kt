package com.unix.app.data.payments

import com.unix.app.data.geo.IpGeoResponse
import java.util.Locale

/**
 * Korapay currently settles collections in NGN, USD, GHS, KES, ZAR, XOF and
 * XAF (per Korapay's own docs — confirm which of these are actually enabled
 * on the university's live account, since availability depends on account
 * configuration, not just the API surface). Every other country is shown
 * pricing in USD as the universal fallback.
 */
enum class SupportedCurrency(val code: String, val symbol: String) {
    NGN("NGN", "₦"),
    GHS("GHS", "GH₵"),
    KES("KES", "KSh"),
    ZAR("ZAR", "R"),
    XOF("XOF", "CFA"),
    XAF("XAF", "FCFA"),
    USD("USD", "$"),
}

private val countryToCurrency: Map<String, SupportedCurrency> = mapOf(
    "NG" to SupportedCurrency.NGN,
    "GH" to SupportedCurrency.GHS,
    "KE" to SupportedCurrency.KES,
    "ZA" to SupportedCurrency.ZAR,
    "CI" to SupportedCurrency.XOF, "SN" to SupportedCurrency.XOF, "BJ" to SupportedCurrency.XOF,
    "CM" to SupportedCurrency.XAF, "GA" to SupportedCurrency.XAF,
)

object CurrencyLocator {

    /** Instant, offline guess from the device/SIM locale. Good enough to
     *  paint the UI before any network call returns. */
    fun fromDeviceLocale(locale: Locale = Locale.getDefault()): SupportedCurrency {
        return countryToCurrency[locale.country.uppercase()] ?: SupportedCurrency.USD
    }

    /** Refines the guess using ipgeolocation.io, which sees the device's
     *  real public IP rather than a possibly-stale locale setting (e.g. a
     *  phone set to en-US locale but physically in Lagos on wifi). */
    fun fromGeoLookup(response: IpGeoResponse): SupportedCurrency {
        response.currency?.code?.uppercase()?.let { code ->
            SupportedCurrency.entries.find { it.code == code }?.let { return it }
        }
        response.location?.countryCode2?.uppercase()?.let { cc ->
            countryToCurrency[cc]?.let { return it }
        }
        return SupportedCurrency.USD
    }
}

/**
 * Display-only conversion so a student browsing from Nairobi sees an
 * approximate KES figure next to the USD base tuition. The ACTUAL amount
 * and currency charged at checkout is decided server-side by /api/pay —
 * these rates are never used to compute what Korapay is told to collect,
 * only what's shown on screen before checkout. Static snapshot rates;
 * replace with a live FX source (or Korapay's own Currency Conversion API)
 * before relying on this for real pricing display.
 */
object FxRates {
    private val perUsd: Map<SupportedCurrency, Double> = mapOf(
        SupportedCurrency.USD to 1.0,
        SupportedCurrency.NGN to 1550.0,
        SupportedCurrency.GHS to 15.5,
        SupportedCurrency.KES to 129.0,
        SupportedCurrency.ZAR to 18.0,
        SupportedCurrency.XOF to 610.0,
        SupportedCurrency.XAF to 610.0,
    )

    fun convertFromUsd(usdAmount: Double, target: SupportedCurrency): Double =
        usdAmount * (perUsd[target] ?: 1.0)
}
