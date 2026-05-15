package com.raithabharosa.hub.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.first
import com.raithabharosa.hub.data.storage.SessionManager
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.raithabharosa.hub.R

@Composable
fun SplashScreen(navController: NavController) {
    val animated by animateFloatAsState(targetValue = 1f, animationSpec = tween(700))

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.app_icon),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size((140 * animated).dp)
                    .graphicsLayer(scaleX = animated, scaleY = animated)
                    .alpha(0.98f)
            )
            Spacer(Modifier.height(14.dp))
            Text(text = stringResource(R.string.splash_title), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = stringResource(R.string.loading), style = MaterialTheme.typography.bodyMedium)
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val session = SessionManager(context)
        // Read current user id, retry a few times to avoid transient DataStore read races (prevents logout on activity recreation)
        var current: Int? = null
        repeat(5) {
            try {
                current = session.currentUserIdFlow.first()
                if (current != null) return@repeat
            } catch (_: Throwable) {}
            delay(250)
        }
        delay(400)
        if (current != null) {
            navController.navigate("main") { popUpTo("splash") { inclusive = true } }
        } else {
            navController.navigate("login") { popUpTo("splash") { inclusive = true } }
        }
    }
}
