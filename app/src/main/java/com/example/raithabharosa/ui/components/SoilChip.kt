package com.example.raitha_bharosa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raitha_bharosa.ui.theme.PrimaryGreen
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

@Composable
fun SoilChip(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PrimaryGreen.copy(alpha = 0.1f)
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun SoilChipPreview() {
    RaithabharosTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SoilChip(
                label = "Temperature",
                value = "25°C",
                icon = "🌡️"
            )
            SoilChip(
                label = "Humidity",
                value = "65%",
                icon = "💧"
            )
            SoilChip(
                label = "Moisture",
                value = "30%",
                icon = "🌱"
            )
        }
    }
}
