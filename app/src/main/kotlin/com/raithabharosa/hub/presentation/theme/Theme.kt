package com.raithabharosa.hub.presentation.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.isSystemInDarkTheme
import com.raithabharosa.hub.data.storage.SessionManager
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


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
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val themeFlow = session.themeFlow
    val theme = themeFlow.collectAsState(initial = "system").value

    val colorScheme = when (theme) {
        "dark" -> darkColorScheme(
            primary = GreenPrimary, onPrimary = NeutralWhite,
            secondary = AmberWarning, onSecondary = NeutralWhite,
            tertiary = RedDanger, onTertiary = NeutralWhite,
            error = RedDanger, onError = NeutralWhite,
            background = NeutralDark, onBackground = NeutralWhite,
            surface = NeutralDark, onSurface = NeutralWhite,
        )
        "light" -> LightColorScheme
        else -> if (isSystemInDarkTheme()) darkColorScheme(
            primary = GreenPrimary, onPrimary = NeutralWhite,
            secondary = AmberWarning, onSecondary = NeutralWhite,
            tertiary = RedDanger, onTertiary = NeutralWhite,
            error = RedDanger, onError = NeutralWhite,
            background = NeutralDark, onBackground = NeutralWhite,
            surface = NeutralDark, onSurface = NeutralWhite,
        ) else LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
