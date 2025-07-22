package com.example.sunday.data.repository

import com.example.sunday.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository {

    private val openMeteoApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val farmsenseApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.farmsense.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun getUvData(latitude: Double, longitude: Double, elevation: Double) =
        openMeteoApiService.getUvData(latitude, longitude, elevation)

    suspend fun getMoonPhase(timestamp: Int) =
        farmsenseApiService.getMoonPhase(timestamp)
}
