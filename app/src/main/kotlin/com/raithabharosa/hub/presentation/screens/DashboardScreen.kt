package com.raithabharosa.hub.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raithabharosa.hub.R
import com.raithabharosa.hub.data.model.CropType
import com.raithabharosa.hub.data.model.ScheduledAction
import com.raithabharosa.hub.data.model.SowingStatus
import com.raithabharosa.hub.presentation.theme.GreenPrimary
import com.raithabharosa.hub.presentation.theme.AmberWarning
import com.raithabharosa.hub.presentation.theme.RedDanger
import com.raithabharosa.hub.presentation.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val colors = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        // ── Branded Header ─────────────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.background,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.brand_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Text(
                        uiState.farmerProfile?.name ?: stringResource(R.string.farmer),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onBackground.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = { viewModel.refreshData(uiState.selectedCropType) }) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = GreenPrimary)
                }
            }
        }

        Divider(modifier = Modifier.fillMaxWidth())

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Crop selector ──────────────────────────────────────
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(cropLabel(uiState.selectedCropType), modifier = Modifier.weight(1f), color = colors.onSurface)
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = colors.onSurface)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    CropType.values().forEach { crop ->
                        DropdownMenuItem(
                            text = { Text(cropLabel(crop)) },
                            onClick = { viewModel.selectCropType(crop); expanded = false }
                        )
                    }
                }
            }

            // ── Farmer Info Card ───────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.farmer_info), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val ringColor = when (uiState.sowingStatus) {
                            SowingStatus.GREEN -> GreenPrimary
                            SowingStatus.AMBER -> AmberWarning
                            SowingStatus.RED   -> RedDanger
                        }
                        val trackColor = colors.onSurface.copy(alpha = 0.1f)
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                            Canvas(modifier = Modifier.size(100.dp)) {
                                val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                                drawArc(trackColor, -90f, 360f, useCenter = false, style = stroke)
                                drawArc(ringColor, -90f, 360f * (uiState.sowingIndex / 100f), useCenter = false, style = stroke)
                            }
                            Text("${uiState.sowingIndex.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = GreenPrimary)
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            LegendItem(GreenPrimary, stringResource(R.string.soil_reads), colors.onSurface)
                            LegendItem(AmberWarning, stringResource(R.string.soil_corrects), colors.onSurface)
                            LegendItem(Color(0xFF4169E1), stringResource(R.string.alter_stats), colors.onSurface)
                            LegendItem(ringColor, statusLabel(uiState.sowingStatus), colors.onSurface)
                        }
                    }
                }
            }

            // ── Farmer Index (Line Chart) ──────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.farmer_index), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                        Surface(shape = RoundedCornerShape(8.dp), color = GreenPrimary.copy(alpha = 0.15f)) {
                            Text(
                                statusLabel(uiState.sowingStatus),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    val points = remember(uiState.sowingIndex) {
                        val b = uiState.sowingIndex
                        listOf(b*.55f, b*.65f, b*.50f, b*.75f, b*.60f, b*.80f, b*.70f, b*.90f, b*.85f, b*1f, b*.95f, b*.88f)
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.sowing_readiness_forecast), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))

                        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                            val w = size.width; val h = size.height
                            val maxVal = 100f
                            val stepX = w / (points.size - 1)
                            fun xAt(i: Int) = i * stepX
                            fun yAt(v: Float) = h - (v / maxVal) * h * 0.8f

                            // Grid lines
                            for (i in 0..4) {
                                val y = h - (i * h * 0.2f)
                                drawLine(
                                    Color.Gray.copy(alpha = 0.1f),
                                    start = androidx.compose.ui.geometry.Offset(0f, y),
                                    end = androidx.compose.ui.geometry.Offset(w, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            val fillPath = Path().apply {
                                moveTo(xAt(0), yAt(points[0]))
                                points.forEachIndexed { i, v ->
                                    if (i > 0) { val cx = (xAt(i-1)+xAt(i))/2f; cubicTo(cx, yAt(points[i-1]), cx, yAt(v), xAt(i), yAt(v)) }
                                }
                                lineTo(xAt(points.size-1), h); lineTo(0f, h); close()
                            }
                            drawPath(fillPath, GreenPrimary.copy(alpha = 0.2f))

                            val linePath = Path().apply {
                                moveTo(xAt(0), yAt(points[0]))
                                points.forEachIndexed { i, v ->
                                    if (i > 0) { val cx = (xAt(i-1)+xAt(i))/2f; cubicTo(cx, yAt(points[i-1]), cx, yAt(v), xAt(i), yAt(v)) }
                                }
                            }
                            drawPath(linePath, GreenPrimary, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))

                            points.forEachIndexed { i, v ->
                                drawCircle(GreenPrimary, radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(xAt(i), yAt(v)))
                            }

                            val amberPath = Path().apply {
                                moveTo(xAt(0), yAt(75f))
                                points.forEachIndexed { i, _ ->
                                    if (i > 0) { val cx = (xAt(i-1)+xAt(i))/2f; cubicTo(cx, yAt(75f), cx, yAt(75f), xAt(i), yAt(75f)) }
                                }
                            }
                            drawPath(amberPath, AmberWarning, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf(R.string.month_jan, R.string.month_feb, R.string.month_mar, R.string.month_apr, R.string.month_may, R.string.month_jun, R.string.month_jul, R.string.month_aug).forEach { monthRes ->
                                Text(stringResource(monthRes), fontSize = 11.sp, color = colors.onSurfaceVariant, fontWeight = FontWeight.Medium)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            LegendItem(GreenPrimary, stringResource(R.string.sowing_readiness), colors.onSurface)
                            LegendItem(AmberWarning, stringResource(R.string.threshold), colors.onSurface)
                        }
                    }
                }
            }

            // ── Data Advices Row ───────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.farmer_info), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val ringColor = when (uiState.sowingStatus) {
                            SowingStatus.GREEN -> GreenPrimary
                            SowingStatus.AMBER -> AmberWarning
                            SowingStatus.RED   -> RedDanger
                        }
                        val trackColor = colors.onSurface.copy(alpha = 0.1f)
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                            Canvas(modifier = Modifier.size(100.dp)) {
                                val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                                drawArc(trackColor, -90f, 360f, useCenter = false, style = stroke)
                                drawArc(ringColor, -90f, 360f * (uiState.sowingIndex / 100f), useCenter = false, style = stroke)
                            }
                            Text("${uiState.sowingIndex.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = GreenPrimary)
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            LegendItem(GreenPrimary, stringResource(R.string.soil_reads), colors.onSurface)
                            LegendItem(AmberWarning, stringResource(R.string.soil_corrects), colors.onSurface)
                            LegendItem(Color(0xFF4169E1), stringResource(R.string.alter_stats), colors.onSurface)
                            LegendItem(ringColor, statusLabel(uiState.sowingStatus), colors.onSurface)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        AdviceStatCard("🌱", "${uiState.sowingIndex.toInt()}%", stringResource(R.string.sowing_readiness_label), GreenPrimary)
                        AdviceStatCard("📋", "${uiState.scheduledActions.size}", stringResource(R.string.scheduled_actions_label_short), Color(0xFF4169E1))
                        AdviceStatCard("☁️", uiState.currentWeatherData?.let { "${it.temperature.toInt()}°C" } ?: "--", stringResource(R.string.weather_today), colors.onSurfaceVariant)
                    }
                }
            }

            // ── Crop Advice Card ───────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.crop_advice), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
                    Text(uiState.cropAdvice?.note ?: uiState.recommendation, fontSize = 14.sp, color = colors.onSurface)
                    if (uiState.cropAdvice != null) {
                        Text("${stringResource(R.string.fertilizer_label)}: ${uiState.cropAdvice.fertilizer}", fontSize = 13.sp, color = colors.onSurfaceVariant)
                        Text("${stringResource(R.string.quantity_label)}: ${uiState.cropAdvice.quantity}", fontSize = 13.sp, color = colors.onSurfaceVariant)
                    }
                }
            }

            // ── Weather Card ───────────────────────────────────────
            uiState.currentWeatherData?.let { w ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenPrimary),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(stringResource(R.string.weather_label), color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            Text("${w.temperature.toInt()}°C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                            Text(w.condition, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("💧 ${w.humidity.toInt()}%", color = Color.White, fontSize = 14.sp)
                            Text("🌧 ${w.rainfall}mm", color = Color.White, fontSize = 14.sp)
                            Text(stringResource(R.string.wind_label, String.format("%.1f", w.windSpeed)), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
            }

            // ── Scheduled Actions ──────────────────────────────────
            if (uiState.scheduledActions.isNotEmpty()) {
                Text(stringResource(R.string.scheduled_actions_label), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.onBackground)
                uiState.scheduledActions.forEach { action: ScheduledAction ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(4.dp), color = GreenPrimary, modifier = Modifier.size(6.dp, 40.dp)) {}
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(action.title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = colors.onSurface)
                                action.notes?.let { Text(it, fontSize = 12.sp, color = colors.onSurfaceVariant) }
                                Text(
                                    java.time.Instant.ofEpochMilli(action.epochMillis).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString(),
                                    fontSize = 11.sp, color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(modifier = Modifier.size(10.dp), shape = RoundedCornerShape(2.dp), color = color) {}
        Text(label, fontSize = 12.sp, color = textColor.copy(alpha = 0.8f))
    }
}

@Composable
private fun AdviceStatCard(icon: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(icon, fontSize = 24.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 14.sp)
    }
}

@Composable
private fun cropLabel(cropType: CropType): String = when (cropType) {
    CropType.SUGARCANE -> stringResource(R.string.crop_sugarcane)
    CropType.RAGI      -> stringResource(R.string.crop_ragi)
    CropType.PADDY     -> stringResource(R.string.crop_paddy)
    CropType.COTTON    -> stringResource(R.string.crop_cotton)
    CropType.CORN      -> stringResource(R.string.crop_corn)
    CropType.WHEAT     -> stringResource(R.string.crop_wheat)
    CropType.SOYBEAN   -> stringResource(R.string.crop_soybean)
    CropType.GROUNDNUT -> stringResource(R.string.crop_groundnut)
    CropType.SUNFLOWER -> stringResource(R.string.crop_sunflower)
    CropType.CHILI     -> stringResource(R.string.crop_chili)
    CropType.TOMATO    -> stringResource(R.string.crop_tomato)
    CropType.ONION     -> stringResource(R.string.crop_onion)
}

@Composable
private fun statusLabel(status: SowingStatus): String = when (status) {
    SowingStatus.GREEN -> stringResource(R.string.ready_to_sow)
    SowingStatus.AMBER -> stringResource(R.string.monitor_conditions)
    SowingStatus.RED   -> stringResource(R.string.not_ready)
}
