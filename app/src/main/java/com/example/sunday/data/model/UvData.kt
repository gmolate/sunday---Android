package com.example.sunday.data.model

import java.util.Date

data class UvData(
    val currentUV: Double = 0.0,
    val maxUV: Double = 0.0,
    val tomorrowMaxUV: Double = 0.0,
    val burnTimeMinutes: Map<Int, Int> = emptyMap(),
    val todaySunrise: Date? = null,
    val todaySunset: Date? = null,
    val tomorrowSunrise: Date? = null,
    val tomorrowSunset: Date? = null,
    val currentAltitude: Double = 0.0,
    val uvMultiplier: Double = 1.0,
    val currentCloudCover: Double = 0.0,
    val currentMoonPhase: Double = 0.0,
    val currentMoonPhaseName: String = "",
    val isVitaminDWinter: Boolean = false,
    val currentLatitude: Double = 0.0,
    val isOfflineMode: Boolean = false,
    val lastSuccessfulUpdate: Date? = null,
    val hasNoData: Boolean = false
)
