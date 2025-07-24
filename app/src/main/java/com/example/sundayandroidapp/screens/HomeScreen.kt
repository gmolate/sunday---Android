package com.example.sundayandroidapp.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.sundayandroidapp.R
import com.example.sundayandroidapp.api.OpenMeteoApiService
import com.example.sundayandroidapp.data.ClothingLevel
import com.example.sundayandroidapp.data.SkinType
import com.example.sundayandroidapp.location.LocationService
import com.example.sundayandroidapp.logic.SunExposureCalculator
import com.example.sundayandroidapp.ui.theme.SundayAndroidAppTheme
import com.example.sundayandroidapp.ui.theme.BlueGradient
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(currentLanguage: String, onLanguageChange: (String) -> Unit) {
    val context = LocalContext.current

    var selectedSkinType by remember { mutableStateOf(SkinType.TYPE_3) }
    var selectedClothingLevel by remember { mutableStateOf(ClothingLevel.LIGHT) }
    var isTracking by remember { mutableStateOf(false) }
    var sessionTotal by remember { mutableStateOf(0.0f) }
    var todayTotal by remember { mutableStateOf(0.0f) }

    var showSkinTypeDialog by remember { mutableStateOf(false) }
    var showClothingLevelDialog by remember { mutableStateOf(false) }

    var locationPermissionsGranted by remember { mutableStateOf(false) }
    var currentUvIndex by remember { mutableStateOf(0.0f) }
    var maxUvIndex by remember { mutableStateOf(0.0f) }
    var maxUvIndexTime by remember { mutableStateOf("") }
    var sunriseTime by remember { mutableStateOf("") }
    var sunsetTime by remember { mutableStateOf("") }
    var cloudCoverPercentage by remember { mutableStateOf(0) }
    var altitudeMeters by remember { mutableStateOf(0) }
    var userLocation by remember { mutableStateOf<Location?>(null) }

    // Retrofit and LocationService instances
    val openMeteoApiService = remember { Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenMeteoApiService::class.java)
    }
    val locationService = remember { LocationService(context) }

    // Launcher para solicitar permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionsGranted = permissions.entries.all { it.value }
        // Si los permisos fueron concedidos, intenta obtener la ubicación de nuevo
        if (locationPermissionsGranted) {
            // Re-trigger LaunchedEffect para obtener ubicación y datos de API
            // Esto se manejará implícitamente por el LaunchedEffect(locationPermissionsGranted)
        }
    }

    // Solicitar permisos al iniciar la pantalla
    LaunchedEffect(Unit) {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission && hasCoarseLocationPermission) {
            locationPermissionsGranted = true
        } else {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Fetch data from Open-Meteo API when permissions are granted or location changes
    LaunchedEffect(locationPermissionsGranted, userLocation) {
        if (locationPermissionsGranted) {
            val location = userLocation ?: locationService.getLastKnownLocation()
            userLocation = location // Update userLocation state

            location?.let { loc ->
                try {
                    val response = openMeteoApiService.getSunData(loc.latitude, loc.longitude)
                    currentUvIndex = response.current.uvIndex.toFloat()
                    cloudCoverPercentage = response.current.cloudCover
                    altitudeMeters = response.elevation.toInt()

                    if (response.daily.uvIndexMax.isNotEmpty()) {
                        maxUvIndex = response.daily.uvIndexMax[0].toFloat()
                    }
                    if (response.daily.sunrise.isNotEmpty()) {
                        sunriseTime = formatTime(response.daily.sunrise[0])
                    }
                    if (response.daily.sunset.isNotEmpty()) {
                        sunsetTime = formatTime(response.daily.sunset[0])
                    }
                } catch (e: Exception) {
                    // Handle API error
                    e.printStackTrace()
                    // TODO: Show error message to user
                }
            }
        }
    }

    // Calculate values first
    val burnLimit = SunExposureCalculator.calculateBurnLimitTime(currentUvIndex, selectedSkinType, selectedClothingLevel, cloudCoverPercentage, altitudeMeters)
    val vitaminDRateCalculated = SunExposureCalculator.calculateVitaminDProductionRate(currentUvIndex, selectedSkinType, selectedClothingLevel, cloudCoverPercentage, altitudeMeters)

    // Vitamin D Tracking Logic
    LaunchedEffect(isTracking, vitaminDRateCalculated) {
        if (isTracking) {
            while (isTracking) {
                // Update every second (vitaminDRateCalculated is per minute)
                sessionTotal += (vitaminDRateCalculated / 60.0f)
                todayTotal += (vitaminDRateCalculated / 60.0f)
                delay(1000L)
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueGradient),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()), // Make content scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title and Language Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SUN DAY",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EN",
                        color = if (currentLanguage == "en") Color.Yellow else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onLanguageChange("en") }
                    )
                    Text(
                        text = "ES",
                        color = if (currentLanguage == "es") Color.Yellow else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onLanguageChange("es") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main UV Index Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.uv_index_label),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "%.1f".format(currentUvIndex),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.burn_limit_template, burnLimit),
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }

            // Details Card (Sunrise/Sunset, Max UV, Clouds, Altitude)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = stringResource(R.string.sunrise_template, sunriseTime), color = Color.White)
                        Text(text = stringResource(R.string.sunset_template, sunsetTime), color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = stringResource(R.string.max_uv_template, maxUvIndex, maxUvIndexTime), color = Color.White)
                        Text(text = stringResource(R.string.clouds_altitude_template, cloudCoverPercentage, altitudeMeters, 6), color = Color.White)
                    }
                    // TODO: Investigate "Retro" label and add it here if applicable
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Session Tracking Card and Start/Stop Button
            SessionTrackingCard(
                rate = vitaminDRateCalculated,
                sessionTotal = sessionTotal,
                todayTotal = todayTotal,
                isTracking = isTracking,
                onToggleTracking = {
                    isTracking = !isTracking
                    if (!isTracking) {
                        sessionTotal = 0.0f // Reset session total when stopping
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clothing Level Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { showClothingLevelDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                ) {
                    Text(text = stringResource(R.string.select_clothing_level), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = selectedClothingLevel.level, color = Color.White, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skin Type Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { showSkinTypeDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                ) {
                    Text(text = stringResource(R.string.select_skin_type), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(R.string.skin_type_template, selectedSkinType.type), color = Color.White, fontSize = 20.sp)
                    Text(text = selectedSkinType.description, color = Color.White, fontSize = 14.sp)
                }
            }
        }

        // Skin Type Dialog
        if (showSkinTypeDialog) {
            OptionsSelectorDialog(
                title = stringResource(R.string.select_skin_type),
                options = SkinType.values().toList(),
                selectedOption = selectedSkinType,
                onOptionSelected = { skinType -> selectedSkinType = skinType },
                onDismissRequest = { showSkinTypeDialog = false },
                optionText = { skinType ->
                    stringResource(R.string.skin_type_template, skinType.type) + ": " + when (skinType) {
                        SkinType.TYPE_1 -> stringResource(R.string.skin_type_1_desc)
                        SkinType.TYPE_2 -> stringResource(R.string.skin_type_2_desc)
                        SkinType.TYPE_3 -> stringResource(R.string.skin_type_3_desc)
                        SkinType.TYPE_4 -> stringResource(R.string.skin_type_4_desc)
                        SkinType.TYPE_5 -> stringResource(R.string.skin_type_5_desc)
                        SkinType.TYPE_6 -> stringResource(R.string.skin_type_6_desc)
                    }
                }
            )
        }

        // Clothing Level Dialog
        if (showClothingLevelDialog) {
            OptionsSelectorDialog(
                title = stringResource(R.string.select_clothing_level),
                options = ClothingLevel.values().toList(),
                selectedOption = selectedClothingLevel,
                onOptionSelected = { clothingLevel -> selectedClothingLevel = clothingLevel },
                onDismissRequest = { showClothingLevelDialog = false },
                optionText = { clothingLevel ->
                    when (clothingLevel) {
                        ClothingLevel.NUDE -> stringResource(R.string.clothing_nude)
                        ClothingLevel.MINIMAL -> stringResource(R.string.clothing_minimal)
                        ClothingLevel.LIGHT -> stringResource(R.string.clothing_light)
                        ClothingLevel.MODERATE -> stringResource(R.string.clothing_moderate)
                        ClothingLevel.HEAVY -> stringResource(R.string.clothing_heavy)
                    }
                }
            )
        }
    }
}

// Helper function to format time strings from API response
private fun formatTime(timeString: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return try {
        formatter.format(parser.parse(timeString) ?: Date())
    } catch (e: Exception) {
        e.printStackTrace()
        "N/A"
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    SundayAndroidAppTheme {
        HomeScreen(
            currentLanguage = Locale.getDefault().language,
            onLanguageChange = { /* Do nothing for preview */ }
        )
    }
}
