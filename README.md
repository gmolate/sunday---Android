# Sunday - Android Version

Android version of [Sunday](https://github.com/jackjackbits/sunday), a comprehensive UV exposure tracking and vitamin D generation app, fully migrated from the original iOS Swift implementation.

## 🌟 Complete Feature Migration Summary

This Android version achieves **100% feature parity** with the original iOS app through systematic three-phase migration:

### 📊 Feature Comparison Table

| Feature | iOS Original | Android Implementation | Status | Implementation Phase |
|---------|-------------|----------------------|---------|---------------------|
| **Core UV Tracking** | ✅ | ✅ | Complete | Base |
| Real-time UV Index | HealthKit + OpenWeather | Open-Meteo API + Room Cache | ✅ Complete | Base |
| Vitamin D Calculation | Custom Swift Algorithm | Kotlin VitaminDCalculator | ✅ Complete | Base |
| Skin Type Selection | 6 Fitzpatrick Types | 6 Fitzpatrick Types | ✅ Complete | Base |
| Clothing Level Adjustment | 4 Levels | 4 Levels (Minimal→Heavy) | ✅ Complete | Base |
| **Health Integration** | ✅ | ✅ | Complete | Base |
| Health Data Sync | HealthKit | Google Fit API | ✅ Complete | Base |
| Session Tracking | Core Data | Room Database | ✅ Complete | Base |
| **Moon Phase System** | ✅ | ✅ | Complete | Phase 1 |
| Moon Phase Display | Manual Calculation | Farmsense API Integration | ✅ Complete | Phase 1 |
| Night Mode Interface | Static Moon Icons | Dynamic Phase Icons + Animation | ✅ Enhanced | Phase 1 |
| Lunar Data Caching | In-Memory | Room Database Cache | ✅ Enhanced | Phase 1 |
| **Solar Timing & Notifications** | ✅ | ✅ | Complete | Phase 2 |
| Solar Noon Calculation | Core Location | SolarCalculator.kt | ✅ Complete | Phase 2 |
| Optimal Sun Notifications | UserNotifications | AlarmManager + NotificationService | ✅ Complete | Phase 2 |
| Location-based Timing | CLLocationManager | LocationManager + Geocoding | ✅ Complete | Phase 2 |
| **Widget System** | ✅ | ✅ | Complete | Phase 2 |
| Home Screen Widget | WidgetKit | Glance for Android | ✅ Complete | Phase 2 |
| UV Index Display | Static Layout | Dynamic UV/Moon Toggle | ✅ Enhanced | Phase 2 |
| Widget Updates | Timeline Provider | Periodic Work Manager | ✅ Complete | Phase 2 |
| Day/Night Widget Modes | Manual Switch | Automatic Solar-based | ✅ Enhanced | Phase 2 |
| **UI/UX Features** | ✅ | ✅ | Complete | Phase 3 |
| Settings Screen | SwiftUI Forms | Jetpack Compose | ✅ Complete | Phase 3 |
| Smooth Animations | SwiftUI Transitions | Compose AnimatedVisibility | ✅ Complete | Phase 3 |
| Material Design | iOS Design System | Material Design 3 | ✅ Platform-optimized | Phase 3 |
| Accessibility Support | VoiceOver | TalkBack + Semantics | ✅ Complete | Phase 3 |
| **Performance & Optimization** | ✅ | ✅ | Enhanced | Phase 3 |
| Memory Management | ARC | Kotlin Coroutines + StateFlow | ✅ Enhanced | Phase 3 |
| Background Processing | BackgroundTasks | WorkManager | ✅ Platform-optimized | Phase 3 |
| Network Monitoring | Network.framework | ConnectivityManager | ✅ Complete | Phase 3 |
| Error Handling | Result Types | Sealed Classes + Exception Handling | ✅ Enhanced | Phase 3 |
| **Development Tools** | ✅ | ✅ | Enhanced | Phase 3 |
| Debug Diagnostics | Basic Logging | DiagnosticService.kt | ✅ Enhanced | Phase 3 |
| Migration System | Core Data Migration | Room Migration + MigrationService | ✅ Enhanced | Phase 3 |
| **Daily Quote Integration** | ✅ | ✅ | In Progress | Phase 4 (Nueva) |

### 🚀 Android-Specific Enhancements

Beyond iOS parity, the Android version includes platform-specific improvements:

- **Enhanced Widget**: Automatic day/night mode switching based on solar calculations
- **Advanced Caching**: Room database with intelligent cleanup and offline support
- **Material Design 3**: Platform-native design language with dynamic theming
- **Notification Channels**: Android-specific notification management
- **Background Optimization**: WorkManager integration for efficient background tasks
- **Permission Management**: Granular Android permission system integration

## 🔧 Technical Architecture

### Core Components
- **MVVM Architecture** with StateFlow for reactive UI updates
- **Room Database** for local data persistence and caching
- **Retrofit + OkHttp** for network operations with automatic retry
- **Jetpack Compose** for modern, declarative UI development
- **Google Fit API** for health data integration
- **WorkManager** for reliable background task execution

### API Integrations
- **Open-Meteo API**: UV index and weather data
- **Farmsense API**: Accurate moon phase calculations
- **Google Fit**: Health data integration
- **Android Location Services**: GPS and network location

### Performance Optimizations
- Intelligent data caching with automatic cleanup
- Coroutine-based async operations
- Memory-efficient state management
- Background task optimization

## 📋 Requirements

### Technical Requirements
- **Android 8.0 (API 26)** or higher
- **Google Play Services** installed and updated
- **Location Services** enabled for accurate UV data
- **Internet Connection** for weather data and API calls

### Permissions
The app requires the following permissions:
- **Location Access**: Precise location for UV index and solar calculations
- **Google Fit**: Health data integration for vitamin D tracking
- **Notifications**: Solar timing alerts and UV warnings, and **Daily Quote Notifications**
- **Internet**: Weather data and API communication
- **Background Processing**: Widget updates and scheduled notifications

## 🚀 Installation & Setup

### Development Setup
```bash
# Clone the repository
git clone [repository-url]
cd sunday---Android

# Open in Android Studio
# Ensure you have Android Studio Arctic Fox or newer

# Sync Gradle dependencies
./gradlew build

# Run the application
./gradlew assembleDebug
```

### API Configuration
1. **Open-Meteo API**: No API key required (free service)
2. **Farmsense API**: No API key required (free service)
3. **Google Fit**: Enable in Google Cloud Console and add your SHA-1 fingerprint

### Build Variants
- **Debug**: Development build with verbose logging
- **Release**: Production build with optimizations

## 🔧 Project Structure

```
app/src/main/java/com/gmolate/sunday/
├── MainActivity.kt                 # App entry point
├── SundayApplication.kt           # Application class
├── model/                         # Data models and database
│   ├── AppDatabase.kt            # Room database configuration
│   ├── UserPreferences.kt        # User settings entity
│   ├── VitaminDSession.kt        # Session tracking
│   ├── CachedUVData.kt          # UV data caching
│   └── CachedMoonData.kt        # Moon phase caching
├── service/                       # Business logic services
│   ├── UVService.kt              # UV data management
│   ├── VitaminDCalculator.kt     # Vitamin D calculations
│   ├── MoonPhaseService.kt       # Moon phase management
│   ├── SolarCalculator.kt        # Solar timing calculations
│   ├── NotificationService.kt    # Notification management (Original)
│   ├── DailyQuoteNotificationService.kt # Daily Quote Notification Service (Nuevo)
│   ├── MigrationService.kt       # Data migration
│   └── DiagnosticService.kt      # Development diagnostics
├── ui/                           # User interface
│   ├── view/
│   │   ├── ContentView.kt        # Main app screen
│   │   └── SettingsView.kt       # Settings screen (Original)
│   │   └── SettingsScreen.kt     # Settings Screen (Nueva)
│   └── viewmodel/
│       └── MainViewModel.kt      # State management
└── widget/
    └── SundayWidget.kt           # Home screen widget (Original)
```

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Integration Tests
```bash
./gradlew connectedAndroidTest
```

### Widget Testing
Test the home screen widget functionality by:
1. Long-pressing on home screen
2. Adding "Sunday UV Widget"
3. Verifying UV/moon phase display

## 📱 Features in Detail

### Main Application
- **Real-time UV tracking** with location-based precision
- **Vitamin D calculation** using scientifically-accurate algorithms
- **Smart notifications** for optimal sun exposure timing
- **Comprehensive settings** for personalization
- **Offline mode** with intelligent data caching
- **Daily motivational quotes** for inspiration

### Home Screen Widget
- **Dynamic display** switching between UV index and moon phases
- **Automatic day/night detection** based on solar calculations
- **Error-resistant updates** with graceful fallback states
- **Material Design 3** styling with system theme integration
- **Optional Daily Quote display** within the widget

### Background Services
- **Solar noon notifications** calculated for precise location
- **Daily motivational quote notifications** at a user-defined time
- **Automatic data updates** with network monitoring
- **Battery optimization** through intelligent scheduling
- **Migration support** for seamless app updates

## 🤝 Contributing

### Development Guidelines
1. Follow Android development best practices
2. Use Kotlin coding conventions
3. Write unit tests for new features
4. Update documentation for significant changes

### Guía de Contribución
¡Gracias por tu interés en contribuir a Sunday Android! Sigue estos pasos para comenzar:

1.  **Fork el Repositorio:** Comienza haciendo un fork del repositorio `gmolate/sunday---Android` en tu cuenta de GitHub.
2.  **Clona tu Fork:** Clona tu repositorio forkeado a tu máquina local.
    ```bash
    git clone https://github.com/tu-usuario/sunday---Android.git
    cd sunday---Android
    ```
3.  **Crea una Nueva Rama:** Crea una nueva rama para tus cambios. El nombre de la rama debe ser descriptivo (ej. `feature/nombre-de-la-feature`, `bugfix/descripcion-del-bug`).
    ```bash
    git checkout -b feature/nombre-de-la-feature
    ```
4.  **Realiza tus Cambios:** Implementa tus mejoras o correcciones. Asegúrate de seguir las pautas de codificación de Kotlin y las mejores prácticas de Android.
5.  **Pruebas:** Escribe o actualiza las pruebas unitarias y de integración para tus cambios.
    ```bash
    ./gradlew test
    ./gradlew connectedAndroidTest
    ```
6.  **Confirma tus Cambios:** Asegúrate de que tus mensajes de commit sean claros y descriptivos.
    ```bash
    git add .
    git commit -m "feat: Añadir nueva funcionalidad X" # O "fix: Corregir bug Y"
    ```
7.  **Sincroniza tu Rama:** Antes de crear un Pull Request, asegúrate de que tu rama esté actualizada con la rama `main` del repositorio original.
    ```bash
    git fetch upstream
    git rebase upstream/main
    ```
8.  **Crea un Pull Request:** Envía tus cambios abriendo un Pull Request desde tu rama a la rama `main` del repositorio `gmolate/sunday---Android`. Proporciona una descripción clara de tus cambios y referencia cualquier issue relevante.

### Convenciones de Commits (Ejemplo)

Utilizamos las [Convenciones de Commits Convencionales](https://www.conventionalcommits.org/en/v1.0.0/) para nuestros mensajes de commit. Algunos ejemplos:

-   `feat: añadir pantalla de configuración de notificaciones`
-   `fix: corregir cálculo de fase lunar`
-   `docs: actualizar guía de contribución en README`
-   `refactor: mejorar la inyección de dependencias`

## 📄 License

This project is licensed under the [MIT License](LICENSE).

## 🙏 Credits

Based on the original [Sunday](https://github.com/jackjackbits/sunday) iOS project by **jackjackbits**.

### Android Migration
- **Complete iOS feature migration** to Android/Kotlin
- **Platform-specific optimizations** for Android ecosystem
- **Enhanced widget functionality** with day/night modes
- **Modern Android architecture** with Jetpack Compose and Room

### Third-party Services
- **Open-Meteo API**: Weather and UV data
- **Farmsense API**: Moon phase calculations
- **Google Fit**: Health data integration
- **Material Design 3**: UI design system

---

**Note**: This Android version maintains complete feature parity with the original iOS app while providing Android-specific enhancements and optimizations. The migration was completed through a systematic three-phase approach ensuring no functionality was lost in translation.
