package com.example.sunday.data.model

enum class ClothingLevel(val descriptionText: String, val exposureFactor: Double) {
    NONE("Nude!", 1.0),
    MINIMAL("Minimal (swimwear)", 0.80),
    LIGHT("Light (shorts, tee)", 0.50),
    MODERATE("Moderate (pants, tee)", 0.30),
    HEAVY("Heavy (pants, sleeves)", 0.10)
}

enum class SkinType(val descriptionText: String, val vitaminDFactor: Double) {
    TYPE1("Very fair", 1.25),
    TYPE2("Fair", 1.1),
    TYPE3("Light", 1.0),
    TYPE4("Medium", 0.7),
    TYPE5("Dark", 0.4),
    TYPE6("Very dark", 0.2)
}
