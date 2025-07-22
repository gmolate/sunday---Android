package com.example.sunday.network

import com.example.sunday.data.model.FarmsenseResponse
import com.example.sunday.data.model.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v1/forecast")
    suspend fun getUvData(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("elevation") elevation: Double,
        @Query("daily") daily: String = "uv_index_max,uv_index_clear_sky_max,sunrise,sunset",
        @Query("hourly") hourly: String = "uv_index,cloud_cover",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 2
    ): OpenMeteoResponse

    @GET("v1/moonphases")
    suspend fun getMoonPhase(
        @Query("d") timestamp: Int
    ): List<FarmsenseResponse>
}
