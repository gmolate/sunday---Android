package com.example.sunday.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sunday.data.model.ClothingLevel
import com.example.sunday.data.model.SkinType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val uvData by viewModel.uvData.collectAsState()

    val gradientColors = getGradientColors()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uvData.hasNoData) {
            Text("No data available.", modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(32.dp)) }
                item { Header() }
                item { Spacer(modifier = Modifier.height(32.dp)) }
                item { UvIndexCard(uvData, uiState) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SessionControl(uiState, viewModel) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { VitaminDCard(uiState) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { ClothingCard(uiState, viewModel) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SkinTypeCard(uiState, viewModel) }
            }
        }
    }
}

@Composable
fun Header() {
    Text(
        text = "SUN DAY",
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun UvIndexCard(uvData: com.example.sunday.data.model.UvData, uiState: UiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("UV INDEX", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            Text(
                String.format("%.1f", uvData.currentUV),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoColumn("BURN LIMIT", "${uvData.burnTimeMinutes[uiState.skinType.ordinal + 1] ?: "---"} min")
                InfoColumn("MAX UVI", String.format("%.1f", uvData.maxUV))
                InfoColumn("SUNRISE", formatTime(uvData.todaySunrise))
                InfoColumn("SUNSET", formatTime(uvData.todaySunset))
            }
        }
    }
}

@Composable
fun SessionControl(uiState: UiState, viewModel: MainViewModel) {
    Button(
        onClick = { viewModel.toggleSession() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(60.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (uiState.isSessionActive) Color.Yellow.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.2f)
        )
    ) {
        Text(if (uiState.isSessionActive) "End" else "Begin", fontSize = 18.sp, color = Color.White)
    }
}

@Composable
fun VitaminDCard(uiState: UiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InfoColumn("RATE", "${uiState.currentVitaminDRate.toInt()} IU/hr")
            InfoColumn("SESSION", "${uiState.sessionVitaminD.toInt()} IU")
            InfoColumn("TODAY", "${uiState.todaysTotalVitaminD.toInt()} IU")
        }
    }
}

@Composable
fun ClothingCard(uiState: UiState, viewModel: MainViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("CLOTHING", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
            Text(uiState.clothingLevel.descriptionText, fontSize = 16.sp, color = Color.White)
        }
    }
    if (showDialog) {
        ClothingPickerDialog(
            onDismiss = { showDialog = false },
            onSelect = {
                viewModel.onClothingLevelChange(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun SkinTypeCard(uiState: UiState, viewModel: MainViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SKIN TYPE", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
            Text(uiState.skinType.descriptionText, fontSize = 16.sp, color = Color.White)
        }
    }

    if (showDialog) {
        SkinTypePickerDialog(
            onDismiss = { showDialog = false },
            onSelect = {
                viewModel.onSkinTypeChange(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun InfoColumn(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun ClothingPickerDialog(onDismiss: () -> Unit, onSelect: (ClothingLevel) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Clothing Level") },
        text = {
            Column {
                ClothingLevel.values().forEach { level ->
                    Text(level.descriptionText, modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(level) }
                        .padding(8.dp))
                }
            }
        },
        confirmButton = { }
    )
}

@Composable
fun SkinTypePickerDialog(onDismiss: () -> Unit, onSelect: (SkinType) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Skin Type") },
        text = {
            Column {
                SkinType.values().forEach { type ->
                    Text(type.descriptionText, modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(type) }
                        .padding(8.dp))
                }
            }
        },
        confirmButton = { }
    )
}


fun formatTime(date: Date?): String {
    return date?.let {
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        formatter.format(it)
    } ?: "--:--"
}

@Composable
fun getGradientColors(): List<Color> {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..4 -> listOf(Color(0xFF0f1c3d), Color(0xFF0a1228)) // Night
        5 -> listOf(Color(0xFF1e3a5f), Color(0xFF2d4a7c)) // Pre-dawn
        6 -> listOf(Color(0xFF3d5a80), Color(0xFF5c7cae)) // Early dawn
        7 -> listOf(Color(0xFF5c7cae), Color(0xFFee9b7a)) // Dawn
        in 8..9 -> listOf(Color(0xFFf4a261), Color(0xFF87ceeb)) // Sunrise
        in 10..15 -> listOf(Color(0xFF4a90e2), Color(0xFF7bb7e5)) // Midday
        in 16..17 -> listOf(Color(0xFF5ca9d6), Color(0xFF87b8d4)) // Late afternoon
        18 -> listOf(Color(0xFFf4a261), Color(0xFFe76f51)) // Golden hour
        19 -> listOf(Color(0xFFe76f51), Color(0xFFc44569)) // Sunset
        20 -> listOf(Color(0xFFc44569), Color(0xFF6a4c93)) // Late sunset
        else -> listOf(Color(0xFF6a4c93), Color(0xFF1e3a5f)) // Dusk
    }
}
