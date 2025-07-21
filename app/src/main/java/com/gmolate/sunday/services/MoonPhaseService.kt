package com.gmolate.sunday.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException

class MoonPhaseService {
    private val api = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.farmsense.net/v1/")
        .build()
        .create(MoonPhaseApi::class.java)
    
    // ...existing code...
    
    suspend fun getMoonPhase(): MoonPhase? {
        return try {
            api.getCurrentMoonPhase()
        } catch (e: HttpException) {
            null
        }
    }
}