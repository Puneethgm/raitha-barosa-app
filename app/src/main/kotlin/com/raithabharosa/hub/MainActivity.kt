package com.raithabharosa.hub

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            // Keep API key out of source to avoid GitHub secret scanning blocks.
            val grokKey = "YOUR_GROQ_API_KEY"
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
