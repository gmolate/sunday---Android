package com.example.sunday.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class SundayWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        val context = androidx.glance.LocalContext.current
        val prefs = context.getSharedPreferences("SundayWidgetPrefs", Context.MODE_PRIVATE)
        val uvIndex = prefs.getFloat("uv_index", 0.0f)
        val burnTime = prefs.getInt("burn_time", 0)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "UV: ${"%.1f".format(uvIndex)}",
                style = TextStyle(color = ColorProvider(Color.White), fontSize = 18.sp)
            )
            Text(
                text = "Burn: $burnTime min",
                style = TextStyle(color = ColorProvider(Color.White), fontSize = 14.sp)
            )
        }
    }
}
