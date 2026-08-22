package com.unix.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoodleClient {

    /** Builds a fresh API client for a given institution base URL, e.g.
     *  "https://learn.unix.edu/". Kept simple (no DI framework) to stay
     *  lightweight. */
    fun create(baseUrl: String, debug: Boolean = false): MoodleApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoodleApi::class.java)
    }
}
