package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandBackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BarChart, contentDescription = null, tint = CyanAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Command Analytics",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                }
                Text(
                    text = "Weekly trends across civic, environmental & security vectors",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }
        }

        // Chart 1: Weekly Incident Volume Bar Chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Civic Incident Frequency (Past 7 Days)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Total: 142 detected events • Peak on Thursday (+38% rain congestion)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val values = listOf(14, 22, 18, 31, 25, 12, 20)
                    val maxVal = 35f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val barWidth = 24.dp.toPx()
                            val spacing = (w - (barWidth * days.size)) / (days.size + 1)

                            // Baseline
                            drawLine(
                                color = CommandBorder,
                                start = Offset(0f, h - 20f),
                                end = Offset(w, h - 20f),
                                strokeWidth = 1.dp.toPx()
                            )

                            values.forEachIndexed { i, v ->
                                val barHeight = (v / maxVal) * (h - 35f)
                                val x = spacing + i * (barWidth + spacing)
                                val y = (h - 20f) - barHeight

                                val barColor = if (v > 25) AlertCritical else CyanAccent

                                drawRect(
                                    color = barColor,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        days.forEach { day ->
                            Text(day, fontSize = 11.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }
        }

        // Chart 2: 24-Hour Air Quality Index (AQI PM2.5) Line Chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Air Quality Trend (AQI PM2.5)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Current: 142 (Moderate)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AlertHigh
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw critical threshold line at AQI 150
                            val thresholdY = h * 0.35f
                            drawLine(
                                color = AlertCritical.copy(alpha = 0.5f),
                                start = Offset(0f, thresholdY),
                                end = Offset(w, thresholdY),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Trend curve
                            val curvePath = Path().apply {
                                moveTo(0f, h * 0.7f)
                                cubicTo(
                                    w * 0.25f, h * 0.65f,
                                    w * 0.50f, h * 0.20f, // Spike
                                    w * 0.75f, h * 0.40f
                                )
                                cubicTo(
                                    w * 0.85f, h * 0.45f,
                                    w * 0.95f, h * 0.55f,
                                    w, h * 0.50f
                                )
                            }
                            drawPath(
                                path = curvePath,
                                color = AlertHigh,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }

                        // Label for threshold
                        Text(
                            text = "⚠️ Unhealthy Threshold (AQI 150)",
                            fontSize = 9.sp,
                            color = AlertCritical,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("00:00", fontSize = 10.sp, color = TextSecondaryDark)
                        Text("06:00", fontSize = 10.sp, color = TextSecondaryDark)
                        Text("12:00 (Peak)", fontSize = 10.sp, color = AlertHigh)
                        Text("18:00", fontSize = 10.sp, color = TextSecondaryDark)
                        Text("Now", fontSize = 10.sp, color = TextSecondaryDark)
                    }
                }
            }
        }

        // Chart 3: Surplus Resource Rescue Breakdown
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Resource Rescue Category Distribution",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )

                    val categories = listOf(
                        Triple("Prepared Warm Meals", 0.55f, TealPulse),
                        Triple("Fresh Produce & Fruit", 0.25f, StatusSafe),
                        Triple("Bakery & Breads", 0.15f, AlertHigh),
                        Triple("Packaged & Pantry", 0.05f, CyanAccent)
                    )

                    categories.forEach { (name, ratio, color) ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name, fontSize = 12.sp, color = TextPrimaryDark)
                                Text("${(ratio * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = color,
                                trackColor = CommandSurfaceHighlight
                            )
                        }
                    }
                }
            }
        }
    }
}
