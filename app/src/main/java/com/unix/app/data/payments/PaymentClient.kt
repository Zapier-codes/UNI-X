package com.unix.app.data.payments

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PaymentClient {

    /** The university's own hosted payment backend — never Korapay's API
     *  directly, and never a place a secret key should live in this app. */
    const val BASE_URL = "https://b-pay-backend.onrender.com/"

    fun create(debug: Boolean = false): PaymentApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymentApi::class.java)
    }
}
