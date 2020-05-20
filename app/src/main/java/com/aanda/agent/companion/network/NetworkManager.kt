package com.aanda.agent.companion.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkManager {

    private val client = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)).build()

    //private const val BASE_URL = "https://5e510330f2c0d300147c034c.mockapi.io/"
    private const val BASE_URL = "https://aacom2-api.q.starberry.com/v1/"



    var gson = GsonBuilder()
            .setLenient()
            .create()
    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory( ToStringConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

    fun <T> buildService(service: Class<T>): T {

        return retrofit.create(service)
    }
}