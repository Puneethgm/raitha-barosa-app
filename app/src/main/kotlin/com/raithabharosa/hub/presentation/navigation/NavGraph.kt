package com.raithabharosa.hub.presentation.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.engine.DataGenerator
import com.raithabharosa.hub.presentation.screens.DashboardScreen
import com.raithabharosa.hub.presentation.theme.RaithaBharosaTheme
import com.raithabharosa.hub.presentation.viewmodel.DashboardViewModel

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
}

@Composable
fun AppNavigation(repository: FarmerRepository, dataGenerator: DataGenerator) {
    RaithaBharosaTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
            composable(Screen.Dashboard.route) {
                val viewModel = DashboardViewModel(repository, dataGenerator)
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
