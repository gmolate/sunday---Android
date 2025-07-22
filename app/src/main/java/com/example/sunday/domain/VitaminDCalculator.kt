package com.example.sunday.domain

import com.example.sunday.data.model.ClothingLevel
import com.example.sunday.data.model.SkinType
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max

class VitaminDCalculator {

    // UV response curve parameters
    private val uvHalfMax = 4.0  // UV index for 50% vitamin D synthesis rate
    private val uvMaxFactor = 3.0 // Maximum multiplication factor at high UV

    fun calculateVitaminDRate(
        uvIndex: Double,
        clothingLevel: ClothingLevel,
        skinType: SkinType,
        userAge: Int?,
        currentAdaptationFactor: Double
    ): Double {
        // Base rate: 21000 IU/hr for Type 3 skin with minimal clothing (80% exposure)
        val baseRate = 21000.0

        // UV factor: Michaelis-Menten-like saturation curve
        val uvFactor = (uvIndex * uvMaxFactor) / (uvHalfMax + uvIndex)

        // Exposure based on clothing coverage
        val exposureFactor = clothingLevel.exposureFactor

        // Skin type affects vitamin D synthesis efficiency
        val skinFactor = skinType.vitaminDFactor

        // Age factor
        val ageFactor = userAge?.let {
            when {
                it <= 20 -> 1.0
                it >= 70 -> 0.25
                else -> max(0.25, 1.0 - (it - 20) * 0.01)
            }
        } ?: 1.0

        // UV quality factor based on time of day
        val currentUVQualityFactor = calculateUVQualityFactor()

        // Final calculation
        return baseRate * uvFactor * exposureFactor * skinFactor * ageFactor * currentUVQualityFactor * currentAdaptationFactor
    }

    fun calculateBurnTime(currentUV: Double, skinType: SkinType): Int {
        val medTimesAtUV1 = mapOf(
            SkinType.TYPE1 to 150.0,
            SkinType.TYPE2 to 250.0,
            SkinType.TYPE3 to 425.0,
            SkinType.TYPE4 to 600.0,
            SkinType.TYPE5 to 850.0,
            SkinType.TYPE6 to 1100.0
        )

        val uvToUse = max(currentUV, 0.1)
        val medTime = medTimesAtUV1[skinType] ?: 425.0
        val fullMED = medTime / uvToUse
        return max(1, fullMED.toInt())
    }

    private fun calculateUVQualityFactor(): Double {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timeDecimal = hour + minute / 60.0
        val solarNoon = 13.0
        val hoursFromNoon = abs(timeDecimal - solarNoon)
        val qualityFactor = exp(-hoursFromNoon * 0.2)

        return max(0.1, minOf(1.0, qualityFactor))
    }

    fun checkVitaminDWinter(latitude: Double, maxUv: Double): Boolean {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0-11

        return if (latitude > 35) {
            when (month) {
                Calendar.NOVEMBER, Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> true
                Calendar.MARCH, Calendar.OCTOBER -> maxUv < 3.0
                else -> false
            }
        } else {
            maxUv < 3.0
        }
    }
}
