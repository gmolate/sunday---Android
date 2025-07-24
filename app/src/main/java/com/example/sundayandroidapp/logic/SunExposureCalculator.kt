package com.example.sundayandroidapp.logic

import com.example.sundayandroidapp.data.ClothingLevel
import com.example.sundayandroidapp.data.SkinType
import kotlin.math.pow

object SunExposureCalculator {

    // Base UV sensitivity for different skin types (Fitzpatrick) - higher value means less sensitive
    private val skinTypeSensitivity = mapOf(
        SkinType.TYPE_1 to 0.7f,
        SkinType.TYPE_2 to 1.0f,
        SkinType.TYPE_3 to 1.5f,
        SkinType.TYPE_4 to 2.0f,
        SkinType.TYPE_5 to 2.5f,
        SkinType.TYPE_6 to 3.0f
    )

    // Clothing protection factors (0.0 = no protection, 1.0 = full protection)
    private val clothingProtection = mapOf(
        ClothingLevel.NUDE to 0.0f,
        ClothingLevel.MINIMAL to 0.1f,
        ClothingLevel.LIGHT to 0.4f,
        ClothingLevel.MODERATE to 0.7f,
        ClothingLevel.HEAVY to 0.9f
    )

    // Vitamin D conversion factor (IU per J/cm^2, this is a simplified placeholder)
    private const val VITAMIN_D_CONVERSION_FACTOR = 1000f // IU per MED (Minimal Erythema Dose)

    /**
     * Calculates the burn limit time in minutes.
     * @param uvIndex Current UV index.
     * @param skinType User's skin type.
     * @param clothingLevel User's clothing level.
     * @param cloudCoverPercentage Cloud cover in percentage (0-100).
     * @param altitudeMeters Altitude in meters.
     * @return Burn limit time in minutes.
     */
    fun calculateBurnLimitTime(
        uvIndex: Float,
        skinType: SkinType,
        clothingLevel: ClothingLevel,
        cloudCoverPercentage: Int,
        altitudeMeters: Int
    ): Int {
        if (uvIndex <= 0) return 0

        val adjustedUV = adjustUVForEnvironment(uvIndex, cloudCoverPercentage, altitudeMeters)
        val effectiveUV = adjustedUV * (1 - (clothingProtection[clothingLevel] ?: 0.0f))

        // MED (Minimal Erythema Dose) for Skin Type 2 is approx 200 J/m^2 = 20 J/cm^2
        // Using a simplified model: MED = UV Index * Exposure Time (minutes)
        // A typical MED for Type 2 skin is reached in ~20 minutes at UV 5.
        // We need to adjust this based on the skin type sensitivity.

        val baseMEDTime = 200f // Reference MED time for a base UV
        val skinFactor = skinTypeSensitivity[skinType] ?: 1.0f

        // Simplified calculation: Time = (BaseMEDTime * SkinFactor) / EffectiveUV
        val burnLimit = (baseMEDTime * skinFactor) / effectiveUV

        return burnLimit.toInt()
    }

    /**
     * Calculates the vitamin D production rate in IU/minute.
     * @param uvIndex Current UV index.
     * @param skinType User's skin type.
     * @param clothingLevel User's clothing level.
     * @param cloudCoverPercentage Cloud cover in percentage (0-100).
     * @param altitudeMeters Altitude in meters.
     * @return Vitamin D production rate in IU/minute.
     */
    fun calculateVitaminDProductionRate(
        uvIndex: Float,
        skinType: SkinType,
        clothingLevel: ClothingLevel,
        cloudCoverPercentage: Int,
        altitudeMeters: Int
    ): Int {
        if (uvIndex <= 0) return 0

        val adjustedUV = adjustUVForEnvironment(uvIndex, cloudCoverPercentage, altitudeMeters)
        val effectiveUV = adjustedUV * (1 - (clothingProtection[clothingLevel] ?: 0.0f))

        // Simplified vitamin D production: proportional to effective UV and skin type sensitivity
        // Higher skin type sensitivity means less light reaches, so need to adjust conversion
        val skinFactorInverse = 1.0f / (skinTypeSensitivity[skinType] ?: 1.0f)

        // This is a highly simplified model. Real vitamin D synthesis is complex.
        // Factors like time of day, angle of sun, exposed skin area, etc., are also crucial.
        val rate = (effectiveUV * VITAMIN_D_CONVERSION_FACTOR * skinFactorInverse) / 100
        return rate.toInt()
    }

    /**
     * Adjusts UV index based on cloud cover and altitude.
     * @param uvIndex Base UV index.
     * @param cloudCoverPercentage Cloud cover in percentage (0-100).
     * @param altitudeMeters Altitude in meters.
     * @return Adjusted UV index.
     */
    private fun adjustUVForEnvironment(
        uvIndex: Float,
        cloudCoverPercentage: Int,
        altitudeMeters: Int
    ): Float {
        // Cloud cover reduction: Simplified model (e.g., 0% clouds = no reduction, 100% clouds = significant reduction)
        val cloudFactor = 1.0f - (cloudCoverPercentage / 100.0f).coerceIn(0.0f, 0.9f)
        var adjustedUV = uvIndex * cloudFactor

        // Altitude adjustment: +6% UV for every 1000m increase (based on common approximations)
        val altitudeAdjustmentFactor = 1.0f + (altitudeMeters / 1000.0f) * 0.06f
        adjustedUV *= altitudeAdjustmentFactor

        return adjustedUV
    }
}
