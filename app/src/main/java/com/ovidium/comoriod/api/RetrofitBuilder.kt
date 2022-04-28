package com.ovidium.comoriod.api

import com.google.gson.GsonBuilder
import com.ovidium.comoriod.BuildConfig
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitBuilder {
    // private const val BASE_URL = "https://api.comori-od.ro/od/"
    private const val BASE_URL = "https://testapi.comori-od.ro/odbeta/"
    // private const val BASE_URL = "https://750e-109-97-21-5.eu.ngrok.io/od/"


    private val retrofit by lazy {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())).build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}