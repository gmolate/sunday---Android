package com.gmolate.sunday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gmolate.sunday.ui.theme.SundayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val useDarkTheme = isSystemInDarkTheme()
            SundayTheme(darkTheme = useDarkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Aquí puedes agregar contenido de ejemplo
    // Por ejemplo:
    // Text(text = "¡Hola Mundo!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SundayTheme {
        MainScreen()
    }
}