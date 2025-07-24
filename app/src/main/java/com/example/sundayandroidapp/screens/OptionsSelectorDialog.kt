package com.example.sundayandroidapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> OptionsSelectorDialog(
    title: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    onDismissRequest: () -> Unit,
    optionText: (T) -> String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title, color = Color.Black, fontSize = 20.sp) },
        text = {
            Column {
                options.forEach {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onOptionSelected(it)
                                onDismissRequest()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedOption == it) Color.LightGray else Color.White
                        )
                    ) {
                        Text(
                            text = optionText(it),
                            modifier = Modifier.padding(16.dp),
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = { /* No confirm button needed for selection */ },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
