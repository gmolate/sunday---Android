package com.example.sundayandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sundayandroidapp.ui.theme.SundayAndroidAppTheme
import com.example.sundayandroidapp.screens.HomeScreen
import com.example.sundayandroidapp.util.LocalizationManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Nueva importación
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() // Activa el splash screen
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var language by remember { mutableStateOf(Locale.getDefault().language) }

            SundayAndroidAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        currentLanguage = language,
                        onLanguageChange = { newLang ->
                            language = newLang
                            LocalizationManager.setLocale(context, newLang)
                            recreate() // Recreate activity to apply language change
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SundayAndroidAppTheme {
        HomeScreen(
            currentLanguage = Locale.getDefault().language,
            onLanguageChange = { /* Do nothing for preview */ }
        )
    }
}
