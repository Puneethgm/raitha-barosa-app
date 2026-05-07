package com.example.raitha_bharosa.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryWhite,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = SecondaryAmber,
    onSecondary = OnSecondaryBlack,
    secondaryContainer = Color(0xFFFFE082),
    onSecondaryContainer = Color(0xFF6D4C00),
    tertiary = Color(0xFF0288D1),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB3E5FC),
    onTertiaryContainer = Color(0xFF01579B),
    error = ErrorRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFF5D0000),
    background = BackgroundLightLeaf,
    onBackground = Color(0xFF1B1B1B),
    surface = SurfaceWhite,
    onSurface = Color(0xFF1B1B1B),
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFF424242),
    outline = OutlineColor,
    outlineVariant = Color(0xFFC7C7C7),
    scrim = Color(0xFF000000),
)

@Composable
fun RaithabharosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
