package com.example.raitha_bharosa.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.raitha_bharosa.domain.model.SeasonLog
import com.example.raitha_bharosa.ui.components.BottomNavBar
import com.example.raitha_bharosa.ui.theme.BackgroundLightLeaf
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val logs by viewModel.logs.collectAsState(initial = emptyList())

    HistoryContent(
        uiState = uiState,
        logs = logs,
        onNavigate = { navController.navigate(it) },
        onAddClick = viewModel::openDialog,
        onDialogClose = viewModel::closeDialog,
        onDateChange = viewModel::updateDate,
        onActionChange = viewModel::updateAction,
        onNotesChange = viewModel::updateNotes,
        onSaveLog = viewModel::addLog
    )
}

@Composable
private fun HistoryContent(
    uiState: HistoryUiState,
    logs: List<SeasonLog>,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    onDialogClose: () -> Unit,
    onDateChange: (String) -> Unit,
    onActionChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveLog: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = "history",
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Log")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLightLeaf),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLightLeaf),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📋",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "No history yet.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Start your first season!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLightLeaf)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Season History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                items(logs.size) { index ->
                    val log = logs[index]
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = log.date,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Crop: ${log.crop}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Action: ${log.actionTaken}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (log.notes.isNotEmpty()) {
                                Text(
                                    text = "Notes: ${log.notes}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isDialogOpen) {
            AlertDialog(
                onDismissRequest = onDialogClose,
                title = { Text("Add Log Entry") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.newDate,
                            onValueChange = onDateChange,
                            label = { Text("Date") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = uiState.newAction,
                            onValueChange = onActionChange,
                            label = { Text("Action") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = uiState.newNotes,
                            onValueChange = onNotesChange,
                            label = { Text("Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = onSaveLog) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDialogClose) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun HistoryScreenPreview() {
    RaithabharosTheme {
        HistoryContent(
            uiState = HistoryUiState(),
            logs = emptyList(),
            onNavigate = {},
            onAddClick = {},
            onDialogClose = {},
            onDateChange = {},
            onActionChange = {},
            onNotesChange = {},
            onSaveLog = {}
        )
    }
}
