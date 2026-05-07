package com.example.raitha_bharosa.ui.screens.auth

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AuthScreen(
    navController: NavController
) {
    var isSignUp by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isSignUp) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToLogin = { isSignUp = false }
            )
        } else {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { isSignUp = true }
            )
        }
    }
}
