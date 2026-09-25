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
import com.example.data.CivicIncident
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun IncidentsScreen(
    incidents: List<CivicIncident>,
    onIncidentClick: (CivicIncident) -> Unit,
    onSimulateAnomaly: () -> Unit,
    modifier: Modifier = Modifier
) {
    var filterSeverity by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = incidents.filter { inc ->
        val matchSev = if (filterSeverity == "ALL") true else inc.severity.equals(filterSeverity, ignoreCase = true)
        val matchSearch = searchQuery.isEmpty() || inc.title.contains(searchQuery, ignoreCase = true) || inc.locationName.contains(searchQuery, ignoreCase = true)
        matchSev && matchSearch
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
                    text = "Civic Incidents",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                Text(
                    text = "Multi-sensor anomaly & event feed",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }

            Button(
                onClick = onSimulateAnomaly,
                colors = ButtonDefaults.buttonColors(containerColor = AlertHigh, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Simulate Anomaly", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by street, area, or incident type...", color = TextSecondaryDark, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark,
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = CommandBorder
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL", "CRITICAL", "HIGH", "MEDIUM", "LOW").forEach { sev ->
                val isSelected = filterSeverity == sev
                FilterChip(
                    selected = isSelected,
                    onClick = { filterSeverity = sev },
                    label = { Text(sev, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CommandCardDark,
                        labelColor = TextSecondaryDark,
                        selectedContainerColor = when (sev) {
                            "CRITICAL" -> AlertCritical
                            "HIGH" -> AlertHigh
                            else -> CyanAccent
                        },
                        selectedLabelColor = if (sev in listOf("CRITICAL", "HIGH")) Color.White else Color.Black
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
            items(filteredList) { incident ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIncidentClick(incident) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = incident.severity, type = incident.severity)
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(text = incident.status, type = incident.status)
                            }
                            Text(
                                text = incident.detectedAt,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = incident.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )

                        Text(
                            text = "📍 ${incident.locationName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanAccent
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = incident.aiAnalysis,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = TextSecondaryDark,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = incident.assignedTeam?.let { "Assigned: $it" } ?: "⚠️ No Team Assigned",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (incident.assignedTeam != null) StatusSafe else AlertHigh
                            )

                            Text(
                                text = "Inspect & Deploy →",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = CyanAccent
                            )
                        }
                    }
                }
            }
        }
    }
}
