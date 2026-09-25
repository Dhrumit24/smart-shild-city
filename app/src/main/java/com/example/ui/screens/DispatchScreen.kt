package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DispatchTask
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DispatchScreen(
    tasks: List<DispatchTask>,
    onAdvanceStatus: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandBackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = CyanAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Smart Dispatch & Routing",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                }
                Text(
                    text = "Live telemetry & green corridor route optimization",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }
        }

        if (tasks.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSafe, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Pending Deliveries", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Text("Accept a surplus match in Resource Rescue to initiate a route.", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                }
            }
        }

        items(tasks) { task ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.taskId,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyanAccent
                        )
                        StatusBadge(text = task.status, type = task.status)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${task.donorName} → ${task.recipientName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimaryDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Driver: ${task.driverName}", fontSize = 12.sp, color = TextSecondaryDark)
                        Text("Distance: ${task.distanceKm} km • ETA: ${task.etaMinutes} min", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusSafe)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tactical Route Canvas Map preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CommandSurfaceDark)
                            .border(1.dp, CommandBorder, RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw route line
                            val routePath = Path().apply {
                                moveTo(w * 0.15f, h * 0.65f)
                                cubicTo(
                                    w * 0.4f, h * 0.25f,
                                    w * 0.65f, h * 0.75f,
                                    w * 0.85f, h * 0.35f
                                )
                            }
                            drawPath(
                                path = routePath,
                                color = CyanAccent.copy(alpha = 0.5f),
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Donor Node
                            drawCircle(color = AlertHigh, radius = 7.dp.toPx(), center = Offset(w * 0.15f, h * 0.65f))

                            // Vehicle position along route
                            val vehiclePos = when (task.status) {
                                "ASSIGNED" -> Offset(w * 0.20f, h * 0.60f)
                                "PICKUP_STARTED" -> Offset(w * 0.30f, h * 0.45f)
                                "IN_TRANSIT" -> Offset(w * 0.55f, h * 0.50f)
                                else -> Offset(w * 0.85f, h * 0.35f)
                            }
                            drawCircle(color = CyanAccent, radius = 9.dp.toPx(), center = vehiclePos)
                            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = vehiclePos)

                            // Shelter Node
                            drawCircle(color = StatusSafe, radius = 7.dp.toPx(), center = Offset(w * 0.85f, h * 0.35f))
                        }

                        // Overlay labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📍 Donor Bay", fontSize = 10.sp, color = AlertHigh)
                            Text("🚚 ${task.driverName}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                            Text("📍 Shelter", fontSize = 10.sp, color = StatusSafe)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Route Advisory: ${task.routeNotes}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multi-stage status buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Stage: ${task.status.replace("_", " ")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )

                        if (task.status != "DELIVERED") {
                            Button(
                                onClick = { onAdvanceStatus(task.id, task.status) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("advance_dispatch_button")
                            ) {
                                Text(
                                    text = when (task.status) {
                                        "ASSIGNED" -> "Start Pickup"
                                        "PICKUP_STARTED" -> "Begin Transit"
                                        "IN_TRANSIT" -> "Confirm Delivered"
                                        else -> "Completed"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
