package com.raithabharosa.hub.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.raithabharosa.hub.data.database.RaithaBharosaDatabase
import com.raithabharosa.hub.data.model.ScheduledAction
import com.raithabharosa.hub.data.repository.ScheduledActionRepository
import com.raithabharosa.hub.data.storage.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.raithabharosa.hub.R
import java.time.Instant
import java.time.ZoneId
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputCenterScreen() {
    val context = LocalContext.current
    val db = RaithaBharosaDatabase.getInstance(context)
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var itemsList by remember { mutableStateOf<List<ScheduledAction>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    val loadItems = suspend {
        val uid = currentUserId
        if (uid != null) {
            val repo = ScheduledActionRepository(db.scheduledActionDao())
            val res = repo.listForUser(uid)
            if (res.isSuccess) itemsList = res.getOrNull() ?: emptyList()
        }
    }

    LaunchedEffect(Unit) {
        session.currentUserIdFlow.collect { id ->
            currentUserId = id
            scope.launch { loadItems() }
        }
    }

    LaunchedEffect(Unit) {
        scope.launch { loadItems() }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.input_center_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(stringResource(R.string.title_label)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.notes_label)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.selected_date, java.time.LocalDate.ofInstant(Instant.ofEpochMilli(dateMillis), ZoneId.systemDefault()).toString()),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.choose_date))
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            scope.launch {
                val uid = currentUserId ?: return@launch
                val repo = ScheduledActionRepository(db.scheduledActionDao())
                val res = repo.add(ScheduledAction(userId = uid, title = title, notes = notes, epochMillis = dateMillis))
                if (res.isSuccess) {
                    message = context.getString(R.string.scheduled_success)
                    itemsList = repo.listForUser(uid).getOrDefault(emptyList())
                    title = ""; notes = ""
                } else message = res.exceptionOrNull()?.message
            }
        }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.schedule_button)) }

        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.upcoming), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn { items(itemsList) { item ->
            Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(item.title)
                    item.notes?.let { Text(it) }
                    Text(java.time.Instant.ofEpochMilli(item.epochMillis).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString())
                }
            }
        } }


            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            dateMillis = datePickerState.selectedDateMillis ?: dateMillis
                            showDatePicker = false
                        }) { Text(stringResource(R.string.ok)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        message?.let { Text(it) }
    }
}
