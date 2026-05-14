package com.raithabharosa.hub.presentation.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary, onPrimary = NeutralWhite,
    secondary = AmberWarning, onSecondary = NeutralWhite,
    tertiary = RedDanger, onTertiary = NeutralWhite,
    error = RedDanger, onError = NeutralWhite,
    background = NeutralWhite, onBackground = NeutralDark,
    surface = NeutralWhite, onSurface = NeutralDark,
)

@Composable
fun RaithaBharosaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColorScheme, content = content)
}
