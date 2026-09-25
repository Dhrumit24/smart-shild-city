package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun IncidentDetailDialog(
    incident: CivicIncident,
    onDismiss: () -> Unit,
    onAssignTeam: (Int, String) -> Unit,
    onResolve: (Int) -> Unit
) {
    var teamInput by remember { mutableStateOf(incident.assignedTeam ?: "Civic Rapid Response Team Alpha") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommandSurfaceDark,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Incident Inspector",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                StatusBadge(text = incident.severity, type = incident.severity)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = incident.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CyanAccent
                )

                Surface(
                    color = CommandCardDark,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Location:", style = MaterialTheme.typography.labelMedium, color = TextSecondaryDark)
                            Text(incident.locationName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = TextPrimaryDark)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Type / Status:", style = MaterialTheme.typography.labelMedium, color = TextSecondaryDark)
                            Text("${incident.type} • ${incident.status}", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Detected:", style = MaterialTheme.typography.labelMedium, color = TextSecondaryDark)
                            Text(incident.detectedAt, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
                        }
                    }
                }

                // AI Intelligence Analysis block
                Card(
                    colors = CardDefaults.cardColors(containerColor = IndigoIntelligence.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IndigoIntelligence.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = IndigoIntelligence, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CityPulse AI Anomaly Analysis",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = IndigoIntelligence
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Confidence: ${incident.confidence}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = CyanAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = incident.aiAnalysis,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Correlated factors: ${incident.relatedEvents}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                    }
                }

                // Assign Team Field
                OutlinedTextField(
                    value = teamInput,
                    onValueChange = { teamInput = it },
                    label = { Text("Deploy Emergency Team", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CommandBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (incident.status != "RESOLVED") {
                    FilledTonalButton(
                        onClick = {
                            onResolve(incident.id)
                            onDismiss()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = StatusSafe.copy(alpha = 0.2f),
                            contentColor = StatusSafe
                        )
                    ) {
                        Text("Mark Resolved")
                    }
                }
                Button(
                    onClick = {
                        onAssignTeam(incident.id, teamInput)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                    modifier = Modifier.testTag("deploy_team_button")
                ) {
                    Text("Deploy Team")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondaryDark)
            }
        }
    )
}

@Composable
fun AddSurplusDialog(
    onDismiss: () -> Unit,
    onSubmit: (donor: String, type: String, quantity: String, location: String, expiry: String) -> Unit
) {
    var donor by remember { mutableStateOf("Metropolitan Convention Center") }
    var foodType by remember { mutableStateOf("Prepared Meals") }
    var quantity by remember { mutableStateOf("90 Hot Meals & Salads") }
    var location by remember { mutableStateOf("Loading Bay 2, City Center") }
    var expiry by remember { mutableStateOf("2h 30m") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommandSurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Recycling, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Surplus Resource",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Surplus-to-Shelter AI will instantly calculate distance and recipient capacity matching.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )

                OutlinedTextField(
                    value = donor,
                    onValueChange = { donor = it },
                    label = { Text("Donor Organization / Restaurant", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CommandBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = foodType,
                    onValueChange = { foodType = it },
                    label = { Text("Resource Category", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CommandBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity & Servings", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CommandBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Pickup Location", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CommandBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = expiry,
                    onValueChange = { expiry = it },
                    label = { Text("Expiry Window", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CommandBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(donor, foodType, quantity, location, expiry)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                modifier = Modifier.testTag("publish_resource_button")
            ) {
                Text("Publish & Match")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}

@Composable
fun VulnerabilityDetailDialog(
    vuln: VulnerabilityFinding,
    onDismiss: () -> Unit,
    onResolve: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommandSurfaceDark,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AlertCritical)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vulnerability Finding",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                }
                StatusBadge(text = vuln.severity, type = vuln.severity)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = vuln.vulnerabilityType,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = AlertCritical
                )

                Surface(
                    color = CommandCardDark,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Target API: ${vuln.apiName}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Endpoint: [${vuln.httpMethod}] ${vuln.endpoint}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanAccent
                        )
                        Text(
                            text = "Status: ${vuln.status} • Detected: ${vuln.detectedAt}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                    }
                }

                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                Text(
                    text = vuln.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )

                Text(
                    text = "Security Evidence (PoC):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = vuln.evidence,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = StatusSafe,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Text(
                    text = "Remediation Guidance:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                Text(
                    text = vuln.recommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = IndigoIntelligence
                )
            }
        },
        confirmButton = {
            if (vuln.status == "OPEN") {
                Button(
                    onClick = {
                        onResolve(vuln.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusSafe, contentColor = Color.White),
                    modifier = Modifier.testTag("mark_resolved_button")
                ) {
                    Text("Mark Resolved")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondaryDark)
            }
        }
    )
}
