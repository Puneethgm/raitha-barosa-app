package com.raithabharosa.hub

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.raithabharosa.hub.data.database.RaithaBharosaDatabase
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.engine.DataGenerator
import com.raithabharosa.hub.domain.engine.SowingIndexEngine
import com.raithabharosa.hub.presentation.navigation.AppNavigation
import com.raithabharosa.hub.presentation.theme.NeutralWhite
import com.raithabharosa.hub.data.repository.GrokRepository
import com.raithabharosa.hub.presentation.components.ChatBot
import com.raithabharosa.hub.presentation.viewmodel.ChatBotViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.remember
import com.raithabharosa.hub.data.storage.LocaleHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocaleHelper.applySavedLocale(this)

        // Create notification channel for task reminders
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_channel",
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled task reminders"
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val database = RaithaBharosaDatabase.getInstance(this)
        val repository = FarmerRepository(
            farmerDao = database.farmerDao(),
            seasonDao = database.seasonDao(),
            soilDao = database.soilDao(),
            dailyActionDao = database.dailyActionDao(),
            sowingIndexEngine = SowingIndexEngine()
        )
        val dataGenerator = DataGenerator()

        setContent {
            // GROQ API key is provided via BuildConfig from local.properties (GROQ_API_KEY)
            val grokKey = BuildConfig.GROQ_API_KEY
            val grokRepo = GrokRepository(grokKey)
            val chatVm = remember { ChatBotViewModel(grokRepo) }

            Surface(modifier = Modifier.fillMaxSize(), color = NeutralWhite) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(repository = repository, dataGenerator = dataGenerator)
                    ChatBot(viewModel = chatVm)
                }
            }
        }
    }
}
