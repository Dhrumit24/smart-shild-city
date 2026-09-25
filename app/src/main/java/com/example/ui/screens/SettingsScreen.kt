package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.UserRole
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    currentRole: UserRole,
    onRoleChangeRequest: () -> Unit,
    usbKeyConnected: Boolean,
    onToggleUsb: () -> Unit,
    modifier: Modifier = Modifier
) {
    var telemetryInterval by remember { mutableStateOf("15 Seconds") }
    var aiThreshold by remember { mutableStateOf(80f) }
    var autoLockdownEnabled by remember { mutableStateOf(true) }

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
                    Icon(Icons.Default.Settings, contentDescription = null, tint = CyanAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "System Settings & Hardware",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                }
                Text(
                    text = "Control center configurations, sensor telemetry & Deadman parameters",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }
        }

        // Active Persona Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Active Persona & Access Role",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(currentRole.badge, fontWeight = FontWeight.Bold, color = CyanAccent)
                            Text(currentRole.displayName, fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Button(
                            onClick = onRoleChangeRequest,
                            colors = ButtonDefaults.buttonColors(containerColor = CommandSurfaceHighlight, contentColor = TextPrimaryDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("switch_role_settings_button")
                        ) {
                            Text("Change Role")
                        }
                    }
                }
            }
        }

        // Deadman's Drive Hardware Token Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SecurityPurple),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Deadman Hardware Key Authentication",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        StatusBadge(
                            text = if (usbKeyConnected) "FIDO2 CONNECTED" else "DETACHED",
                            type = if (usbKeyConnected) "SAFE" else "CRITICAL"
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Serial: DD-SEC-2026-FIDO2-90214 • SHA-256 Verified",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulate Hardware Key State",
                            fontSize = 12.sp,
                            color = TextPrimaryDark
                        )
                        Button(
                            onClick = onToggleUsb,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (usbKeyConnected) AlertHigh else StatusSafe,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (usbKeyConnected) "Detach Key" else "Insert Key", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // AI Correlation Thresholds
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CityPulse AI Anomaly Sensitivity",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Minimum confidence score to escalate to critical alert: ${aiThreshold.toInt()}%",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Slider(
                        value = aiThreshold,
                        onValueChange = { aiThreshold = it },
                        valueRange = 50f..95f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = CommandSurfaceHighlight
                        )
                    )
                }
            }
        }

        // Architecture Attribution Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SmartShield Integrated Architecture",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Text("• CityPulse: Live civic monitoring & multimodal anomaly detection", fontSize = 12.sp, color = TextSecondaryDark)
                    Text("• Surplus-to-Shelter: AI matching, route optimization & food security", fontSize = 12.sp, color = TextSecondaryDark)
                    Text("• SentinelAPI: OWASP Top 10 API scanning & vulnerability management", fontSize = 12.sp, color = TextSecondaryDark)
                    Text("• Deadman's Drive: Hardware-key zero-trust defense & automated lockdown", fontSize = 12.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Version 1.0.0 • Production Build", fontSize = 11.sp, color = CyanAccent)
                }
            }
        }
    }
}
