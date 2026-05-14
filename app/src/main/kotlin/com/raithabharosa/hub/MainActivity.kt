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
            Surface(modifier = Modifier.fillMaxSize(), color = NeutralWhite) {
                AppNavigation(repository = repository, dataGenerator = dataGenerator)
            }
        }
    }
}
