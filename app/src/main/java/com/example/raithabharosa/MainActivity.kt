package com.example.raithabharosa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.core.os.LocaleListCompat
import com.example.raitha_bharosa.data.repository.AuthRepository
import com.example.raitha_bharosa.data.repository.FarmerRepository
import com.example.raitha_bharosa.ui.navigation.AppNavGraph
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var farmerRepository: FarmerRepository

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val loggedIn = authRepository.isLoggedIn.first()
            val farmer = farmerRepository.getFirstFarmer()
            if (farmer != null && loggedIn) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(farmer.language)
                )
            }

            setContent {
                RaithabharosTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        AppNavGraph(
                            navController = navController,
                            startDestination = when {
                                !loggedIn -> "auth"
                                farmer == null -> "onboarding"
                                else -> "dashboard"
                            }
                        )
                    }
                }
            }
        }
    }
}
