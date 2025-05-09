package com.example.replicateai.data.api

import com.example.replicateai.util.Constants
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val certificatePinner = CertificatePinner.Builder()
        .add("api.replicate.com", "sha256/NWxPc3cQXnBFZaWyCmODJWW+9b2aJsW1WN3ojyi0glU=")
        .build()
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .certificatePinner(certificatePinner)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}