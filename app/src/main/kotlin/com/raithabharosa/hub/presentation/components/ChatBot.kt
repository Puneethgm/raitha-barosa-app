package com.raithabharosa.hub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.foundation.gestures.detectDragGestures
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raithabharosa.hub.R
import com.raithabharosa.hub.presentation.viewmodel.ChatBotViewModel
import com.raithabharosa.hub.presentation.viewmodel.ChatMessage
import com.raithabharosa.hub.presentation.viewmodel.Sender

@Composable
fun ChatBot(viewModel: ChatBotViewModel) {
    var open by remember { mutableStateOf(false) }
    var fullscreen by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val parentWidthPx = with(density) { maxWidth.toPx() }
        val parentHeightPx = with(density) { maxHeight.toPx() }
        val fabSizeDp = 56.dp
        val fabSizePx = with(density) { fabSizeDp.toPx() }
        val marginPx = with(density) { 16.dp.toPx() }
        var fabOffset by remember { mutableStateOf(Offset.Zero) }
        LaunchedEffect(maxWidth, maxHeight) {
            if (fabOffset == Offset.Zero) {
                fabOffset = Offset(parentWidthPx - fabSizePx - marginPx, parentHeightPx * 0.3f)
            }
        }
        // Chat sheet
        if (open) {
            val listState = rememberLazyListState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .let { if (fullscreen) it.fillMaxHeight() else it.height(360.dp) }
                    .align(Alignment.BottomCenter)
                    .padding(if (fullscreen) 0.dp else 12.dp),
                shape = if (fullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.chatbot_image),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RaithaBot", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = { fullscreen = !fullscreen }, modifier = Modifier.height(32.dp)) {
                            Text(if (fullscreen) "Exit" else "Fullscreen", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Close", modifier = Modifier.clickable { open = false; fullscreen = false }, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val msgs by viewModel.messages.collectAsState()
                    LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                        items(msgs) { m ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (m.sender == Sender.USER) Arrangement.End else Arrangement.Start
                            ) {
                                val bubbleColor = if (m.sender == Sender.USER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                val textColor = if (m.sender == Sender.USER) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(6.dp),
                                    colors = CardDefaults.cardColors(containerColor = bubbleColor)
                                ) {
                                    Text(m.text, modifier = Modifier.padding(8.dp), color = textColor)
                                }
                            }
                        }
                    }

                    LaunchedEffect(key1 = msgs.size) {
                        if (msgs.isNotEmpty()) listState.animateScrollToItem(msgs.size - 1)
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            modifier = Modifier.weight(1f).height(56.dp),
                            placeholder = { Text("Ask farming question...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            maxLines = 2,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Button(
                            onClick = { viewModel.sendUserMessage(input); input = "" },
                            modifier = Modifier.height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                        ) { Text("Send", fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) }
                    }
                }
            }
        }

        // Show floating button only when popup is closed.
        if (!open) {
            FloatingActionButton(
                onClick = { open = true },
                modifier = Modifier
                    .offset { IntOffset(fabOffset.x.roundToInt(), fabOffset.y.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            val newX = (fabOffset.x + dragAmount.x).coerceIn(0f, parentWidthPx - fabSizePx)
                            val newY = (fabOffset.y + dragAmount.y).coerceIn(0f, parentHeightPx - fabSizePx)
                            fabOffset = Offset(newX, newY)
                        }
                    }
                    .size(fabSizeDp)
                    .padding(0.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Image(
                    painter = painterResource(R.drawable.chatbot_image),
                    contentDescription = "Chat",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
    }
}
