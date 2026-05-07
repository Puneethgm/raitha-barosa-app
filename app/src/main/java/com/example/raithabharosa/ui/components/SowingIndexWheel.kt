package com.example.raitha_bharosa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme
import com.example.raitha_bharosa.ui.theme.ReadyAmber
import com.example.raitha_bharosa.ui.theme.SowGreen
import com.example.raitha_bharosa.ui.theme.WaitRed

@Composable
fun SowingIndexWheel(
    index: Int,
    modifier: Modifier = Modifier
) {
    val progress = index / 100f
    val color = when {
        index <= 40 -> WaitRed
        index <= 70 -> ReadyAmber
        else -> SowGreen
    }

    val actionText = when {
        index <= 40 -> "ಕಾಯಿರಿ\nWait"
        index <= 70 -> "ಸಿದ್ಧರಾಗಿ\nGet Ready"
        else -> "ಬಿತ್ತನೆ ಮಾಡಿ\nSow Now"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(200.dp),
                color = color,
                strokeWidth = 16.dp,
                trackColor = Color.LightGray
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(140.dp)
                    .background(Color.White, CircleShape)
                    .align(Alignment.Center)
            ) {
                Text(
                    text = index.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "%",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = actionText,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
fun SowingIndexWheelPreview() {
    RaithabharosTheme {
        Column(Modifier.background(Color.White)) {
            SowingIndexWheel(35)
            SowingIndexWheel(55)
            SowingIndexWheel(85)
        }
    }
}
