import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class VitaminDCalculator {
    // ...existing code...

    fun calculateVitaminDProduction(
        uvIndex: Double,
        exposureTimeMinutes: Int,
        skinType: Int
    ): Double {
        // Use kotlin.math functions
        val baseProduction = uvIndex * exposureTimeMinutes * 10.0
        val skinFactor = when (skinType) {
            1 -> 0.5
            2 -> 0.7
            3 -> 0.9
            4 -> 1.1
            5 -> 1.3
            6 -> 1.5
            else -> 1.0
        }
        return baseProduction * skinFactor
    }

    // ...existing code...
}