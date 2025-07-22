package com.example.sunday.ui

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.health.connect.client.permission.HealthPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunday.data.model.ClothingLevel
import com.example.sunday.data.model.SkinType
import com.example.sunday.data.model.UvData
import com.example.sunday.data.repository.Repository
import com.example.sunday.domain.HealthConnectManager
import com.example.sunday.domain.LocationManager
import com.example.sunday.domain.VitaminDCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class MainViewModel(
    private val repository: Repository,
    private val locationManager: LocationManager,
    private val vitaminDCalculator: VitaminDCalculator,
    private val healthConnectManager: HealthConnectManager,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _uvData = MutableStateFlow(UvData())
    val uvData: StateFlow<UvData> = _uvData

    val permissions = setOf(
        HealthPermission.getReadPermission(androidx.health.connect.client.records.VitaminDRecord::class),
        HealthPermission.getWritePermission(androidx.health.connect.client.records.VitaminDRecord::class)
    )

    init {
        fetchData()
        readTodaysVitaminD()
    }

    fun fetchData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                locationManager.getCurrentLocation()?.let { location ->
                    val response = repository.getUvData(location.latitude, location.longitude, location.altitude)
                    val altitudeMultiplier = 1.0 + (location.altitude / 1000.0 * 0.1)
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val todayIndex = response.daily.time.indexOf(today)

                    val todaySunrise = parseDate(response.daily.sunrise.getOrNull(todayIndex))
                    val todaySunset = parseDate(response.daily.sunset.getOrNull(todayIndex))

                    val tomorrowIndex = todayIndex + 1
                    val tomorrowSunrise = parseDate(response.daily.sunrise.getOrNull(tomorrowIndex))
                    val tomorrowSunset = parseDate(response.daily.sunset.getOrNull(tomorrowIndex))


                    val now = Calendar.getInstance()
                    val hour = now.get(Calendar.HOUR_OF_DAY)
                    val currentUv = response.hourly?.uvIndex?.getOrNull(hour) ?: 0.0
                    val maxUv = response.daily.uvIndexMax.getOrNull(todayIndex) ?: 0.0
                    val tomorrowMaxUv = response.daily.uvIndexMax.getOrNull(tomorrowIndex) ?: 0.0
                    val cloudCover = response.hourly?.cloudCover?.getOrNull(hour) ?: 0.0

                    val isVitaminDWinter = vitaminDCalculator.checkVitaminDWinter(location.latitude, maxUv)

                    val moonPhaseResponse = repository.getMoonPhase(
                        (System.currentTimeMillis() / 1000).toInt()
                    )
                    val moonPhase = moonPhaseResponse.firstOrNull()


                    _uvData.value = _uvData.value.copy(
                        currentUV = currentUv * altitudeMultiplier,
                        maxUV = maxUv * altitudeMultiplier,
                        tomorrowMaxUV = tomorrowMaxUv * altitudeMultiplier,
                        todaySunrise = todaySunrise,
                        todaySunset = todaySunset,
                        tomorrowSunrise = tomorrowSunrise,
                        tomorrowSunset = tomorrowSunset,
                        currentAltitude = location.altitude,
                        uvMultiplier = altitudeMultiplier,
                        currentCloudCover = cloudCover,
                        currentMoonPhase = moonPhase?.illumination ?: 0.0,
                        currentMoonPhaseName = moonPhase?.phase ?: "",
                        isVitaminDWinter = isVitaminDWinter,
                        currentLatitude = location.latitude,
                        lastSuccessfulUpdate = Date(),
                        hasNoData = false,
                        isOfflineMode = false
                    )
                    calculateBurnTimes()
                    calculateVitaminDRate()
                    updateWidget()
                } ?: run {
                    _uvData.value = _uvData.value.copy(hasNoData = true)
                }
            } catch (e: Exception) {
                _uvData.value = _uvData.value.copy(hasNoData = true)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onSkinTypeChange(skinType: SkinType) {
        _uiState.value = _uiState.value.copy(skinType = skinType)
        calculateBurnTimes()
        calculateVitaminDRate()
        updateWidget()
    }

    fun onClothingLevelChange(clothingLevel: ClothingLevel) {
        _uiState.value = _uiState.value.copy(clothingLevel = clothingLevel)
        calculateVitaminDRate()
    }

    fun toggleSession() {
        viewModelScope.launch {
            val isSessionActive = !_uiState.value.isSessionActive
            _uiState.value = _uiState.value.copy(isSessionActive = isSessionActive)
            if (isSessionActive) {
                _uiState.value = _uiState.value.copy(sessionStartTime = Date())
                startVitaminDUpdates()
            } else {
                val sessionVitaminD = _uiState.value.sessionVitaminD
                if (sessionVitaminD > 0) {
                    healthConnectManager.writeVitaminD(
                        sessionVitaminD,
                        ZonedDateTime.ofInstant(
                            _uiState.value.sessionStartTime!!.toInstant(),
                            ZonedDateTime.now().zone
                        ),
                        ZonedDateTime.now()
                    )
                }
                _uiState.value = _uiState.value.copy(sessionVitaminD = 0.0)
                readTodaysVitaminD()
            }
        }
    }

    private fun startVitaminDUpdates() {
        viewModelScope.launch {
            while (_uiState.value.isSessionActive) {
                val elapsedSeconds = (Date().time - (_uiState.value.sessionStartTime?.time ?: 0)) / 1000
                val vitaminDPerSecond = _uiState.value.currentVitaminDRate / 3600
                _uiState.value = _uiState.value.copy(
                    sessionVitaminD = vitaminDPerSecond * elapsedSeconds
                )
                kotlinx.coroutines.delay(1000)
            }
        }
    }


    private fun calculateBurnTimes() {
        val burnTimes = SkinType.values().associateWith {
            vitaminDCalculator.calculateBurnTime(_uvData.value.currentUV, it)
        }
        _uvData.value = _uvData.value.copy(burnTimeMinutes = burnTimes.mapKeys { it.key.ordinal + 1 })
    }

    private fun calculateVitaminDRate() {
        val rate = vitaminDCalculator.calculateVitaminDRate(
            uvIndex = _uvData.value.currentUV,
            clothingLevel = _uiState.value.clothingLevel,
            skinType = _uiState.value.skinType,
            userAge = _uiState.value.userAge,
            currentAdaptationFactor = _uiState.value.currentAdaptationFactor
        )
        _uiState.value = _uiState.value.copy(currentVitaminDRate = rate)
    }

    private fun readTodaysVitaminD() {
        viewModelScope.launch {
            val startOfDay = ZonedDateTime.now().toLocalDate().atStartOfDay(ZonedDateTime.now().zone)
            val endOfDay = startOfDay.plusDays(1)
            val records = healthConnectManager.readVitaminDHistory(startOfDay.toInstant(), endOfDay.toInstant())
            val total = records.sumOf { it.amount.inInternationalUnits }
            _uiState.value = _uiState.value.copy(todaysTotalVitaminD = total)
        }
    }


    private fun parseDate(dateString: String?): Date? {
        return dateString?.let {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(it)
        }
    }

    fun requestPermissions() {
        viewModelScope.launch {
            healthConnectManager.requestPermissions(permissions)
        }
    }

    private fun updateWidget() {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("SundayWidgetPrefs", Context.MODE_PRIVATE)
            with(prefs.edit()) {
                putFloat("uv_index", _uvData.value.currentUV.toFloat())
                putInt("burn_time", _uvData.value.burnTimeMinutes[_uiState.value.skinType.ordinal + 1] ?: 0)
                apply()
            }

            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceAppWidgetManager.getGlanceIds(SundayWidget::class.java)
            glanceIds.forEach { glanceId ->
                SundayWidget().update(context, glanceId)
            }
        }
    }
}

data class UiState(
    val isLoading: Boolean = true,
    val skinType: SkinType = SkinType.TYPE3,
    val clothingLevel: ClothingLevel = ClothingLevel.LIGHT,
    val isSessionActive: Boolean = false,
    val sessionStartTime: Date? = null,
    val sessionVitaminD: Double = 0.0,
    val currentVitaminDRate: Double = 0.0,
    val todaysTotalVitaminD: Double = 0.0,
    val userAge: Int? = null,
    val currentAdaptationFactor: Double = 1.0
)
