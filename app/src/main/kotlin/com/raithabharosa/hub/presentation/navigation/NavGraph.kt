package com.raithabharosa.hub.presentation.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import com.raithabharosa.hub.presentation.screens.*
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
import com.raithabharosa.hub.R
import androidx.annotation.StringRes

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
}

sealed class BottomNavItem(val route: String, @StringRes val labelRes: Int, val icon: ImageVector) {
    object Dashboard : BottomNavItem("dashboard", R.string.dashboard_title, Icons.Filled.Home)
    object InputCenter : BottomNavItem("input_center", R.string.input_center_title, Icons.Filled.Edit)
    object Calendar : BottomNavItem("calendar", R.string.krishi_calendar_title, Icons.Filled.DateRange)
    object History : BottomNavItem("history", R.string.settings_title, Icons.Filled.Settings)
}

@Composable
fun AppNavigation(repository: FarmerRepository, dataGenerator: DataGenerator) {
    RaithaBharosaTheme {
        val authNavController = rememberNavController()
        val items = listOf(
            BottomNavItem.Dashboard,
            BottomNavItem.InputCenter,
            BottomNavItem.Calendar,
            BottomNavItem.History
        )
        // top-level nav: splash -> auth -> main app
        NavHost(navController = authNavController, startDestination = "splash") {
            composable("splash") { SplashScreen(authNavController) }
            
            composable("login") { LoginScreen(authNavController) }
            composable("signup") { SignupScreen(authNavController) }

            composable("main") {
                val mainNavController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry = mainNavController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry.value?.destination?.route
                            items.forEach { item ->
                                NavigationBarItem(
                                    selected = currentRoute == item.route,
                                    onClick = { mainNavController.navigate(item.route) {
                                        popUpTo(mainNavController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    } },
                                    icon = { Icon(imageVector = item.icon as ImageVector, contentDescription = androidx.compose.ui.res.stringResource(item.labelRes)) },
                                    label = { Text(androidx.compose.ui.res.stringResource(item.labelRes)) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = mainNavController,
                        startDestination = BottomNavItem.Dashboard.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomNavItem.Dashboard.route) {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val weatherSvc = retrofit2.Retrofit.Builder().baseUrl("https://api.open-meteo.com/").addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create()).build().create(com.raithabharosa.hub.data.network.WeatherService::class.java)
                            val weatherRepo = com.raithabharosa.hub.data.repository.WeatherRepository(weatherSvc)
                            val scheduledRepo = com.raithabharosa.hub.data.repository.ScheduledActionRepository(com.raithabharosa.hub.data.database.RaithaBharosaDatabase.getInstance(context).scheduledActionDao())
                            val session = com.raithabharosa.hub.data.storage.SessionManager(context)
                            val fused = com.raithabharosa.hub.data.location.FusedLocationProvider(context)
                            val viewModel = DashboardViewModel(repository, dataGenerator, weatherRepo, scheduledRepo, session, fused)
                            DashboardScreen(viewModel = viewModel)
                        }
                        composable(BottomNavItem.InputCenter.route) {
                            InputCenterScreen()
                        }
                        composable(BottomNavItem.Calendar.route) {
                            KrishiCalendarScreen()
                        }
                        composable(BottomNavItem.History.route) {
                            SettingsScreen(navController = authNavController)
                        }
                    }
                }
            }
        }
    }
}
