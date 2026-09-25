package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.ScreenNav
import com.example.ui.UserRole
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    currentRole: UserRole,
    incidents: List<CivicIncident>,
    resources: List<ResourceOffer>,
    alerts: List<AlertItem>,
    vulnerabilities: List<VulnerabilityFinding>,
    onNavigate: (ScreenNav) -> Unit,
    onIncidentClick: (CivicIncident) -> Unit,
    onSimulateAnomaly: () -> Unit,
    onAddSurplusClick: () -> Unit,
    onTriggerScanClick: () -> Unit,
    isLockdownActive: Boolean,
    modifier: Modifier = Modifier
) {
    val activeIncidents = incidents.filter { it.status != "RESOLVED" }
    val criticalAlerts = alerts.filter { it.severity == "CRITICAL" }
    val openVulns = vulnerabilities.filter { it.status == "OPEN" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandBackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Welcome & System Pulse Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Morning, ${currentRole.displayName} 👋",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Real-time command center telemetry online",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }

                Surface(
                    color = CommandCardDark,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isLockdownActive) AlertCritical else StatusSafe)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLockdownActive) "LOCKDOWN" else "ALL SYSTEMS GO",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = if (isLockdownActive) AlertCritical else StatusSafe
                        )
                    }
                }
            }
        }

        // Summary KPI Grid (4 Pillars: Incidents, Alerts, Resources, Security)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricKpiCard(
                        title = "🚨 Incidents",
                        value = "${activeIncidents.size}",
                        subtitle = "${incidents.count { it.severity == "CRITICAL" }} Critical active",
                        icon = Icons.Default.Warning,
                        accentColor = AlertCritical,
                        onClick = { onNavigate(ScreenNav.INCIDENTS) },
                        modifier = Modifier.weight(1f)
                    )
                    MetricKpiCard(
                        title = "⚠️ Alerts",
                        value = "${alerts.size}",
                        subtitle = "${criticalAlerts.size} high priority",
                        icon = Icons.Default.NotificationsActive,
                        accentColor = AlertHigh,
                        onClick = { onNavigate(ScreenNav.ALERTS) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricKpiCard(
                        title = "♻️ Resources",
                        value = "${resources.size}",
                        subtitle = "${resources.count { it.status == "MATCHED" }} Auto-matched",
                        icon = Icons.Default.Recycling,
                        accentColor = TealPulse,
                        onClick = { onNavigate(ScreenNav.RESOURCES) },
                        modifier = Modifier.weight(1f)
                    )
                    MetricKpiCard(
                        title = "🔐 Security",
                        value = "${openVulns.size}",
                        subtitle = "Score: 87% Protected",
                        icon = Icons.Default.Security,
                        accentColor = SecurityPurple,
                        onClick = { onNavigate(ScreenNav.SECURITY) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions Row (Hackathon Demo Drivers)
        item {
            Column {
                Text(
                    text = "Command Actions",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Button(
                            onClick = onSimulateAnomaly,
                            colors = ButtonDefaults.buttonColors(containerColor = AlertHigh, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("simulate_anomaly_button")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate City Anomaly", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        Button(
                            onClick = onAddSurplusClick,
                            colors = ButtonDefaults.buttonColors(containerColor = TealPulse, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_surplus_button")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Post Food Surplus", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        Button(
                            onClick = onTriggerScanClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("scan_api_button")
                        ) {
                            Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Sentinel Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Tactical Map Callout Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(ScreenNav.CIVIC_MAP) }
                    .testTag("dashboard_map_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = CyanAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live Tactical Map",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }
                        Text(
                            text = "Expand View →",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stylized mini radar view
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(CommandSurfaceDark, CommandSurfaceHighlight)
                                )
                            )
                            .border(1.dp, CommandBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AlertCritical))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Traffic Hub", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AlertHigh))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Air Quality", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(StatusSafe))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Shelters", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to open interactive full-screen multi-layer GIS map",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent
                            )
                        }
                    }
                }
            }
        }

        // CityPulse AI Insights Spotlight
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoIntelligence.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(ScreenNav.AI_INSIGHTS) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = IndigoIntelligence)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Intelligence Spotlight",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }
                        Text(
                            text = "View Analytics →",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = IndigoIntelligence
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = IndigoIntelligence.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "⚠️ Traffic Congestion Anomaly (Zone A)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "84% Confidence",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CyanAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Correlated observations: Heavy rain alert (1.2 km away) + bus line 12 delay. Correlation distinguished from causal factor.",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        // Recent Critical Alerts List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Alerts",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                TextButton(onClick = { onNavigate(ScreenNav.ALERTS) }) {
                    Text("View All (${alerts.size})", color = CyanAccent, fontSize = 12.sp)
                }
            }
        }

        items(alerts.take(3)) { alert ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (alert.targetScreen) {
                            "INCIDENTS" -> onNavigate(ScreenNav.INCIDENTS)
                            "SECURITY" -> onNavigate(ScreenNav.SECURITY)
                            "RESOURCES" -> onNavigate(ScreenNav.RESOURCES)
                            else -> onNavigate(ScreenNav.ALERTS)
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(text = alert.severity, type = alert.severity)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = alert.timeAgo,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = TextSecondaryDark
                    )
                }
            }
        }
    }
}
