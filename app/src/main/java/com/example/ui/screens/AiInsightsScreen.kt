package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CivicIncident
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AiInsightsScreen(
    incidents: List<CivicIncident>,
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
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = IndigoIntelligence)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CityPulse AI Engine",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                }
                Text(
                    text = "Multimodal civic anomaly detection & correlation analysis",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }
        }

        // Correlation vs Causation Guardrail Callout (Mandatory per CityPulse brief)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Guidance",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Analytical Framework: Correlation ≠ Causation",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "CityPulse detects temporal and spatial correlations across sensor streams. These observations highlight statistical co-occurrence to guide human dispatch decisions, not unverified single-cause attribution.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Active Anomaly Findings
        item {
            Text(
                text = "Detected Civic Anomalies",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        items(incidents.size) { idx ->
            val inc = incidents[idx]
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
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
                            text = inc.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        StatusBadge(text = "${inc.confidence}% Conf", type = "HIGH")
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sector: ${inc.locationName} • Type: ${inc.type}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccent
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = inc.aiAnalysis,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar for AI confidence
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Model Confidence Metric", fontSize = 11.sp, color = TextSecondaryDark)
                            Text("${inc.confidence}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { inc.confidence / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (inc.confidence > 90) AlertCritical else CyanAccent,
                            trackColor = CommandSurfaceHighlight
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Correlated Factor Tags
                    Surface(
                        color = CommandSurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = IndigoIntelligence, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Correlated sensor: ${inc.relatedEvents}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextPrimaryDark
                            )
                        }
                    }
                }
            }
        }
    }
}
