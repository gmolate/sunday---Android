[📖 Read the detailed methodology](METHODOLOGY.md) | [🔒 Privacy Policy](PRIVACY.md)

<img height="500" alt="SunDay_1290x2796_v2" src="https://github.com/user-attachments/assets/b712cc98-1cc5-4e6f-8297-cabf8f801013" />

# Sunday - Android Version

This is the Android version of [Sunday](https://github.com/jackjackbits/sunday), a powerful application for tracking UV exposure and Vitamin D generation, rebuilt from the ground up in Kotlin for the Android platform.

This version is designed to be completely self-contained within the `android/` directory. 

## Core Features

This Android implementation currently provides the core functionalities essential for sun exposure tracking:

| Feature                  | Android Implementation              | Status      |
| ------------------------ | ----------------------------------- | ----------- |
| **Real-time UV Tracking**    | Open-Meteo API Integration          | Implemented |
| **Vitamin D Calculation**  | `SunExposureCalculator.kt` Logic    | Implemented |
| **Burn Limit Calculation**   | Based on Fitzpatrick Scale & UV     | Implemented |
| **Skin Type Selection**      | Fitzpatrick Scale Types I-VI        | Implemented |
| **Clothing Level Adjustment**| Nude, Minimal, Light, etc.          | Implemented |
| **Manual Refresh**         | Floating Action Button (FAB)        | Implemented |
| **Bilingual Support**      | English & Spanish (Dynamic)         | Implemented |
| **In-App Methodology**     | Displays methodology text           | Implemented |

**Note:** Advanced features from the original iOS version such as Google Fit integration, database persistence (Room), widgets, and notifications are **not yet implemented** in this version.

## Technical Architecture

The application is built using a modern Android tech stack, focusing on simplicity and performance.

-   **UI:** Fully built with **Jetpack Compose** for a modern, declarative user interface.
-   **State Management:** State is managed directly within `@Composable` functions using `remember` and `mutableStateOf`.
-   **Networking:** **Retrofit** and **Gson** are used for efficient and reliable communication with the Open-Meteo API.
-   **Location:** Leverages **Google Play Services**' FusedLocationProviderClient for accurate location data.
-   **Core Logic:** All sun exposure calculations are handled in a dedicated `SunExposureCalculator.kt` object, ensuring logic is centralized and testable.

## Requirements

-   **Android 6.0 (API 23)** or higher.
-   **Google Play Services** installed and up-to-date.
-   **Location Services** enabled for accurate UV data.
-   **Internet Connection** for fetching weather data.

## Project Structure

The project structure is organized by feature/responsibility:

```

app/src/main/kotlin/com/example/sundayandroidapp/
├── MainActivity.kt                 # App entry point
├── api/                            # Retrofit API interface and data models
│   ├── OpenMeteoApiService.kt
│   └── OpenMeteoResponse.kt
├── data/                           # Enums and data classes (SkinType, ClothingLevel)
│   ├── SkinType.kt
│   └── ClothingLevel.kt
├── location/                       # Location services
│   └── LocationService.kt
├── logic/                          # Core calculation logic
│   └── SunExposureCalculator.kt
└── screens/                        # UI Composables
    ├── HomeScreen.kt
    ├── MethodologyScreen.kt
    ├── SessionTrackingCard.kt
    └── OptionsSelectorDialog.kt

```

## How to Build

```
bash
# Clone the main repository
git clone [repository-url]
cd [repository-name]/android

# Open the 'android' directory in Android Studio
# Allow Gradle to sync and download dependencies

# Build the debug version
./gradlew assembleDebug

```

## License

This project is licensed under the [MIT License](LICENSE). 
by @gmolate
```