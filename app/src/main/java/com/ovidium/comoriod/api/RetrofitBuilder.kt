package com.ovidium.comoriod.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    // private const val BASE_URL = "https://api.comori-od.ro/od/"
    private const val BASE_URL = "https://testapi.comori-od.ro/odbeta/"
    // private const val BASE_URL = "https://750e-109-97-21-5.eu.ngrok.io/od/"

    private val retrofit by lazy  {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())).build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}