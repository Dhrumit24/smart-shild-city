package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.AlertItem
import com.example.ui.ScreenNav
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AlertsScreen(
    alerts: List<AlertItem>,
    onAcknowledgeAlert: (Int) -> Unit,
    onNavigate: (ScreenNav) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredAlerts = alerts.filter { alert ->
        if (selectedFilter == "ALL") true
        else if (selectedFilter == "UNACKNOWLEDGED") !alert.isAcknowledged
        else alert.severity.equals(selectedFilter, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CommandBackgroundDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Alert Center",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                Text(
                    text = "Consolidated civic, security, resource and AI alerts",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL", "UNACKNOWLEDGED", "CRITICAL", "HIGH", "MEDIUM", "RESOLVED").forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CommandCardDark,
                        labelColor = TextSecondaryDark,
                        selectedContainerColor = when (filter) {
                            "CRITICAL" -> AlertCritical
                            "HIGH" -> AlertHigh
                            else -> CyanAccent
                        },
                        selectedLabelColor = if (filter in listOf("CRITICAL", "HIGH")) Color.White else Color.Black
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = CommandBorder,
                        selectedBorderColor = CyanAccent,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredAlerts) { alert ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (alert.isAcknowledged) CommandCardDark.copy(alpha = 0.6f) else CommandCardDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (alert.severity == "CRITICAL" && !alert.isAcknowledged) AlertCritical else CommandBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = alert.severity, type = alert.severity)
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(text = alert.category, type = "INFO")
                            }
                            Text(
                                text = alert.timeAgo,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )

                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!alert.isAcknowledged) {
                                Button(
                                    onClick = { onAcknowledgeAlert(alert.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CommandSurfaceHighlight,
                                        contentColor = TextPrimaryDark
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("ack_alert_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Acknowledge", fontSize = 11.sp)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DoneAll, contentDescription = null, tint = StatusSafe, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Acknowledged", fontSize = 11.sp, color = StatusSafe)
                                }
                            }

                            alert.targetScreen?.let { target ->
                                TextButton(
                                    onClick = {
                                        when (target) {
                                            "INCIDENTS" -> onNavigate(ScreenNav.INCIDENTS)
                                            "SECURITY" -> onNavigate(ScreenNav.SECURITY)
                                            "RESOURCES" -> onNavigate(ScreenNav.RESOURCES)
                                            else -> {}
                                        }
                                    }
                                ) {
                                    Text("Investigate →", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
