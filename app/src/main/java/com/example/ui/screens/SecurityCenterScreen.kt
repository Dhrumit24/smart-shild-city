package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.ScreenNav
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun SecurityCenterScreen(
    apiTargets: List<ApiTarget>,
    vulnerabilities: List<VulnerabilityFinding>,
    securityEvents: List<SecurityEvent>,
    isSystemLocked: Boolean,
    failedAttempts: Int,
    usbConnected: Boolean,
    activeThreats: Int,
    onNavigate: (ScreenNav) -> Unit,
    onVulnerabilityClick: (VulnerabilityFinding) -> Unit,
    onToggleLockdown: () -> Unit,
    onToggleUsb: () -> Unit,
    onTriggerBackup: () -> Unit,
    onBlockIp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openVulns = vulnerabilities.filter { it.status == "OPEN" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandBackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = SecurityPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SentinelAPI & Threat Defense",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                    }
                    Text(
                        text = "Deadman's Drive automated protection & API vulnerability auditing",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }

                Button(
                    onClick = { onNavigate(ScreenNav.API_SCANNER) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("launch_scanner_button")
                ) {
                    Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("API Scanner", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Deadman's Drive Threat Protection Console Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isSystemLocked) AlertCritical.copy(alpha = 0.15f) else CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSystemLocked) AlertCritical else SecurityPurple),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSystemLocked) Icons.Default.Lock else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (isSystemLocked) AlertCritical else StatusSafe
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Deadman Threat Protection",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }
                        StatusBadge(
                            text = if (isSystemLocked) "SYSTEM LOCKDOWN" else "SHIELD ACTIVE",
                            type = if (isSystemLocked) "CRITICAL" else "SAFE"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Threat metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Failed Auth Logins", fontSize = 11.sp, color = TextSecondaryDark)
                            Text("$failedAttempts / 5 Threshold", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AlertHigh)
                        }
                        Column {
                            Text("Hardware Key Token", fontSize = 11.sp, color = TextSecondaryDark)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (usbConnected) StatusSafe else AlertCritical))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (usbConnected) "Verified (FIDO2)" else "Detached", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (usbConnected) StatusSafe else AlertCritical)
                            }
                        }
                        Column {
                            Text("Active Ingress Threats", fontSize = 11.sp, color = TextSecondaryDark)
                            Text("$activeThreats Detected", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (activeThreats > 0) AlertCritical else StatusSafe)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Threat Response Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onToggleLockdown,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSystemLocked) StatusSafe else AlertCritical,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("toggle_lockdown_button")
                        ) {
                            Text(if (isSystemLocked) "Release Lock" else "🔒 Lockdown", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onBlockIp,
                            colors = ButtonDefaults.buttonColors(containerColor = CommandSurfaceHighlight, contentColor = TextPrimaryDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("block_ip_button")
                        ) {
                            Text("🚫 Block IP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onTriggerBackup,
                            colors = ButtonDefaults.buttonColors(containerColor = CommandSurfaceHighlight, contentColor = TextPrimaryDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("backup_button")
                        ) {
                            Text("💾 Backup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = onToggleUsb,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CommandSurfaceHighlight)
                        ) {
                            Icon(Icons.Default.Usb, contentDescription = "Simulate Key", tint = if (usbConnected) StatusSafe else AlertHigh)
                        }
                    }
                }
            }
        }

        // Audited API Gateways
        item {
            Text(
                text = "Registered Municipal APIs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        items(apiTargets) { api ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = api.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        StatusBadge(text = api.status, type = api.status)
                    }

                    Text(
                        text = api.baseUrl,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccent
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Endpoints: ${api.endpointsCount}", fontSize = 11.sp, color = TextSecondaryDark)
                        Text("Critical: ${api.criticalCount} • High: ${api.highCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AlertCritical)
                        Text("Score: ${api.securityScore}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    }
                }
            }
        }

        // Active Vulnerability Findings
        item {
            Text(
                text = "Active Vulnerability Findings (${openVulns.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        items(openVulns) { vuln ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVulnerabilityClick(vuln) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(text = vuln.severity, type = vuln.severity)
                        Text(
                            text = vuln.detectedAt,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = vuln.vulnerabilityType,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = AlertCritical
                    )

                    Text(
                        text = "[${vuln.httpMethod}] ${vuln.endpoint}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanAccent
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = vuln.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextSecondaryDark,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "View PoC Evidence & Remediate →",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyanAccent
                        )
                    }
                }
            }
        }
    }
}
