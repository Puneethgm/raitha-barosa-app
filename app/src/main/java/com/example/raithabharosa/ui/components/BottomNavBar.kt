package com.example.raitha_bharosa.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import com.example.raitha_bharosa.R
import com.example.raitha_bharosa.ui.theme.PrimaryGreen
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

sealed class BottomNavItem(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: String
) {
    data object Dashboard : BottomNavItem("dashboard", R.string.dashboard, "🌱")
    data object Calendar : BottomNavItem("krishi_calendar", R.string.calendar, "📅")
    data object Input : BottomNavItem("input_center", R.string.input_center, "🧪")
    data object History : BottomNavItem("history", R.string.history, "📋")
    data object Settings : BottomNavItem("settings", R.string.settings, "⚙️")
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Calendar,
        BottomNavItem.Input,
        BottomNavItem.History,
        BottomNavItem.Settings
    )

    NavigationBar(
        modifier = modifier.background(Color.White),
        containerColor = Color.White,
        tonalElevation = androidx.compose.material3.NavigationBarDefaults.Elevation
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                label = {
                    Text(
                        text = stringResource(item.labelResId),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                icon = {
                    Text(text = item.icon, fontSize = 20.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryGreen,
                    selectedTextColor = PrimaryGreen,
                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    RaithabharosTheme {
        BottomNavBar(
            currentRoute = "dashboard",
            onNavigate = {}
        )
    }
}
