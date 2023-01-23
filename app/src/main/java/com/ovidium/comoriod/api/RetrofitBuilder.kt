package com.ovidium.comoriod.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.ovidium.comoriod.BuildConfig
import com.ovidium.comoriod.MainActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    const val BASE_URL = "https://api.comori-od.ro/od/"
    // const val BASE_URL = "https://testapi.comori-od.ro/od/"
    // const val BASE_URL = "https://newapi.comori-od.ro/od/"
    // const val BASE_URL = "https://80e4-109-97-21-5.eu.ngrok.io/od/"

    private const val connectTimeout = 60L
    private const val readTimeout = 60L
    private const val writeTimeout = 60L
    lateinit var appContext: Context

    private val retrofit by lazy {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        System.getProperty("http.agent")

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(UserAgentInterceptor(appContext))
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .writeTimeout(readTimeout, TimeUnit.SECONDS)
            .readTimeout(writeTimeout, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}