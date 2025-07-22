package com.example.sunday.data.model

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    val daily: DailyData,
    val hourly: HourlyData?
)

data class DailyData(
    val time: List<String>,
    @SerializedName("uv_index_max")
    val uvIndexMax: List<Double>,
    @SerializedName("uv_index_clear_sky_max")
    val uvIndexClearSkyMax: List<Double>?,
    val sunrise: List<String>,
    val sunset: List<String>
)

data class HourlyData(
    val time: List<String>,
    @SerializedName("uv_index")
    val uvIndex: List<Double>,
    @SerializedName("cloud_cover")
    val cloudCover: List<Double>?
)

data class FarmsenseResponse(
    @SerializedName("Phase")
    val phase: String,
    @SerializedName("Illumination")
    val illumination: Double,
    @SerializedName("Age")
    val age: Double,
    @SerializedName("Distance")
    val distance: Double,
    @SerializedName("Azimuth")
    val azimuth: Double,
    @SerializedName("Altitude")
    val altitude: Double,
    @SerializedName("EclipticLongitude")
    val eclipticLongitude: Double,
    @SerializedName("EclipticLatitude")
    val eclipticLatitude: Double
)
