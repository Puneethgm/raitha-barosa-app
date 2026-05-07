package com.example.raitha_bharosa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.raitha_bharosa.ui.screens.auth.AuthScreen
import com.example.raitha_bharosa.ui.screens.onboarding.OnboardingScreen
import com.example.raitha_bharosa.ui.screens.dashboard.DashboardScreen
import com.example.raitha_bharosa.ui.screens.inputcenter.InputCenterScreen
import com.example.raitha_bharosa.ui.screens.calendar.KrishiCalendarScreen
import com.example.raitha_bharosa.ui.screens.history.HistoryScreen
import com.example.raitha_bharosa.ui.screens.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = "auth"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("auth") {
            AuthScreen(navController)
        }
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable("dashboard") {
            DashboardScreen(navController)
        }
        composable("input_center") {
            InputCenterScreen(navController)
        }
        composable("krishi_calendar") {
            KrishiCalendarScreen(navController)
        }
        composable("history") {
            HistoryScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}
