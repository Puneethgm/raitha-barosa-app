package com.raithabharosa.hub.presentation.screens

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.foundation.BorderStroke
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.raithabharosa.hub.data.database.RaithaBharosaDatabase
import com.raithabharosa.hub.data.model.ScheduledAction
import com.raithabharosa.hub.data.repository.ScheduledActionRepository
import com.raithabharosa.hub.data.storage.SessionManager
import com.raithabharosa.hub.presentation.theme.GreenPrimary
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.raithabharosa.hub.R
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState


fun format12Hour(hour: Int, minute: Int): String {
    val period = if (hour >= 12) "PM" else "AM"
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return "${displayHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $period"
}

fun scheduleNotification(context: Context, task: ScheduledAction) {
    try {
        android.util.Log.d("ScheduleNotif", "Scheduling task: ${task.title} for ${task.epochMillis}")

        val now = System.currentTimeMillis()
        val delayMillis = task.epochMillis - now

        if (delayMillis <= 0) {
            android.util.Log.w("ScheduleNotif", "Scheduled time is in the past")
            return
        }

        val workData = Data.Builder()
            .putInt("taskId", task.id)
            .putString("title", task.title)
            .putString("notes", task.notes ?: "Time for your task!")
            .build()

        val notificationWork = OneTimeWorkRequestBuilder<com.raithabharosa.hub.NotificationWorker>()
            .setInputData(workData)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "notification_${task.id}",
            androidx.work.ExistingWorkPolicy.KEEP,
            notificationWork
        )

        android.util.Log.d("ScheduleNotif", "Work scheduled with delay: ${delayMillis}ms")
    } catch (e: Exception) {
        android.util.Log.e("ScheduleNotif", "Error: ${e.message}", e)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputCenterScreen() {
    val context = LocalContext.current
    val db = RaithaBharosaDatabase.getInstance(context)
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var hourOfDay by remember { mutableStateOf(12) }
    var minute by remember { mutableStateOf(0) }
    var itemsList by remember { mutableStateOf<List<ScheduledAction>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    LaunchedEffect(Unit) {
        try {
            val id = session.currentUserIdFlow.first()
            currentUserId = id
            if (id != null) {
                val repo = ScheduledActionRepository(db.scheduledActionDao())
                val res = repo.listForUser(id)
                if (res.isSuccess) itemsList = res.getOrNull() ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        // Green header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.schedule_tasks), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Input form card ────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(stringResource(R.string.new_task), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(stringResource(R.string.title_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text(stringResource(R.string.notes_label)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Date selector
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            val istZone = ZoneId.of("Asia/Kolkata")
                            Text(LocalDate.ofInstant(Instant.ofEpochMilli(dateMillis), istZone).toString() + " (IST)")
                        }

                        // Time selector - Google style
                        Button(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(),
                            border = BorderStroke(1.dp, GreenPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                format12Hour(hourOfDay, minute) + " IST",
                                color = colors.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    val uid = currentUserId ?: return@launch
                                    if (title.isBlank()) {
                                        message = "Title is required"
                                        return@launch
                                    }

                                    // Combine date and time using IST (Indian Standard Time)
                                    val istZone = ZoneId.of("Asia/Kolkata") // IST timezone
                                    val dateTime = java.time.LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(dateMillis),
                                        istZone
                                    ).withHour(hourOfDay).withMinute(minute).withSecond(0)
                                    val scheduledMillis = dateTime.atZone(istZone).toInstant().toEpochMilli()

                                    val repo = ScheduledActionRepository(db.scheduledActionDao())
                                    val task = ScheduledAction(userId = uid, title = title, notes = notes, epochMillis = scheduledMillis)
                                    val res = repo.add(task)
                                    if (res.isSuccess) {
                                        message = "Task scheduled ✅"
                                        // Schedule notification with sound
                                        res.getOrNull()?.let { scheduleNotification(context, it) }
                                        itemsList = repo.listForUser(uid).getOrDefault(emptyList())
                                        title = ""
                                        notes = ""
                                        dateMillis = System.currentTimeMillis()
                                        hourOfDay = 12
                                        minute = 0
                                    } else message = res.exceptionOrNull()?.message
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.schedule_button), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        message?.let {
                            Text(it, color = GreenPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
            }

            // ── Scheduled tasks list ───────────────────────────────
            if (itemsList.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.upcoming_tasks),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.onSurface
                    )
                }

                items(itemsList.sortedByDescending { it.epochMillis }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = colors.onSurface
                                )
                                if (!item.notes.isNullOrBlank()) {
                                    Text(
                                        item.notes,
                                        fontSize = 12.sp,
                                        color = colors.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                                val istZone = ZoneId.of("Asia/Kolkata")
                                val dateTime = java.time.LocalDateTime.ofInstant(Instant.ofEpochMilli(item.epochMillis), istZone)
                                val dateStr = dateTime.toLocalDate().toString()
                                val timeStr = dateTime.toLocalTime().toString().substring(0, 5) // HH:mm format
                                Text(
                                    "$dateStr at $timeStr IST",
                                    fontSize = 11.sp,
                                    color = GreenPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val repo = ScheduledActionRepository(db.scheduledActionDao())
                                        repo.delete(item)
                                        itemsList = itemsList.filter { it.id != item.id }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tasks scheduled yet",
                            color = colors.onSurfaceVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }

    // Date picker dialog
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

    // Time picker dialog - Scrollable style like Google Clock
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text(
                    format12Hour(hourOfDay, minute),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour picker
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier
                                .height(100.dp)
                                .width(60.dp),
                            state = androidx.compose.foundation.lazy.rememberLazyListState(maxOf(0, hourOfDay - 1)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(24) { hour ->
                                val isSelected = hour == hourOfDay
                                Text(
                                    hour.toString().padStart(2, '0'),
                                    fontSize = if (isSelected) 32.sp else 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GreenPrimary else colors.onSurfaceVariant,
                                    modifier = Modifier
                                        .clickable { hourOfDay = hour }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }

                    Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)

                    // Minute picker
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier
                                .height(100.dp)
                                .width(60.dp),
                            state = androidx.compose.foundation.lazy.rememberLazyListState(maxOf(0, minute - 1)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(60) { minIndex ->
                                val min = minIndex
                                val isSelected = min == minute
                                Text(
                                    min.toString().padStart(2, '0'),
                                    fontSize = if (isSelected) 32.sp else 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GreenPrimary else colors.onSurfaceVariant,
                                    modifier = Modifier
                                        .clickable { minute = min }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showTimePicker = false }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                    Text(stringResource(R.string.set))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
