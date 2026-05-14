package com.raithabharosa.hub.presentation.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raithabharosa.hub.data.model.SowingStatus
import com.raithabharosa.hub.presentation.theme.*
import com.raithabharosa.hub.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeutralLight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(24.dp)
        ) {
            Column {
                Text("Dashboard", style = MaterialTheme.typography.headlineLarge, color = NeutralWhite)
                Text(uiState.farmerProfile?.name ?: "Farmer", style = MaterialTheme.typography.bodyMedium, color = GreenLight)
            }
            IconButton(onClick = { viewModel.refreshData() }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(Icons.Default.Refresh, null, tint = NeutralWhite)
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = when (uiState.sowingStatus) {
                        SowingStatus.GREEN -> GreenVeryLight
                        SowingStatus.AMBER -> AmberLight
                        SowingStatus.RED -> RedLight
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Sowing Index", style = MaterialTheme.typography.titleMedium)
                        Text(
                            String.format("%.0f%%", uiState.sowingIndex),
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            when (uiState.sowingStatus) {
                                SowingStatus.GREEN -> "Ready to Sow"
                                SowingStatus.AMBER -> "Monitor Conditions"
                                SowingStatus.RED -> "Not Ready"
                            }
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = GreenVeryLight
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recommendation", style = MaterialTheme.typography.titleSmall)
                        Text(uiState.recommendation, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}
