package com.unix.app.data.openedx

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenEdxClient {

    /** Builds a client for a given Open edX LMS base URL, e.g.
     *  "https://mooc.your-institution.edu/". Auth uses OAuth2/JWT bearer
     *  tokens per Open edX convention — pass a token provider so a fresh
     *  token is attached to every request without re-building the client. */
    fun create(baseUrl: String, tokenProvider: () -> String?, debug: Boolean = false): OpenEdxApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        val auth = Interceptor { chain ->
            val token = tokenProvider()
            val request = if (token != null) {
                chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
            } else chain.request()
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenEdxApi::class.java)
    }
}
