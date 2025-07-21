import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContentView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sunday Weather App",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Moon & UV Index Tracker",
            fontSize = 18.sp,
            color = OnBackgroundColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContentViewPreview() {
    MaterialTheme {
        ContentView()
    }
}

// fun ContentView() {
//     setContent {
//         // Tu UI Compose aquí
//     }
// }
