package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlin.math.sqrt

enum class MapLayerFilter(val label: String) {
    ALL("All Layers"),
    TRAFFIC("Traffic & Incidents"),
    AIR_QUALITY("Air Quality"),
    WEATHER("Weather & Flood"),
    RESOURCES("Surplus Food"),
    SECURITY("Security Alerts"),
    SHELTERS("Safe Shelters")
}

@Composable
fun CivicMapScreen(
    incidents: List<CivicIncident>,
    resources: List<ResourceOffer>,
    shelters: List<ShelterEntity>,
    onIncidentSelected: (CivicIncident) -> Unit,
    onAcceptResource: (ResourceOffer) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(MapLayerFilter.ALL) }
    var activeMarkerInfo by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedIncidentRef by remember { mutableStateOf<CivicIncident?>(null) }
    var selectedResourceRef by remember { mutableStateOf<ResourceOffer?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CommandBackgroundDark)
    ) {
        // Map Controls & Layer Selector
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tactical City Grid",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Real-time sensor feeds • GPS: 37.7749° N, 122.4194° W",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CommandCardDark,
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
                                .background(CyanAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE SENSORS: 48 ONLINE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = CyanAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MapLayerFilter.values().forEach { filter ->
                    val isSelected = filter == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CommandCardDark,
                            labelColor = TextSecondaryDark,
                            selectedContainerColor = CyanAccent,
                            selectedLabelColor = Color.Black
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) CyanAccent else CommandBorder,
                            selectedBorderColor = CyanAccent,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
        }

        // Tactical Canvas Map Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CommandSurfaceDark)
                .border(1.dp, CommandBorder, RoundedCornerShape(16.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(incidents, resources, shelters, selectedFilter) {
                        detectTapGestures { offset ->
                            val width = size.width.toFloat()
                            val height = size.height.toFloat()

                            // Check collision with incidents
                            val clickedIncident = incidents.firstOrNull { inc ->
                                val matchesFilter = when (selectedFilter) {
                                    MapLayerFilter.ALL -> true
                                    MapLayerFilter.TRAFFIC -> inc.type == "TRAFFIC"
                                    MapLayerFilter.AIR_QUALITY -> inc.type == "AIR_QUALITY"
                                    MapLayerFilter.WEATHER -> inc.type == "WEATHER"
                                    MapLayerFilter.SECURITY -> inc.type == "SECURITY"
                                    MapLayerFilter.RESOURCES -> inc.type == "RESOURCE"
                                    else -> false
                                }
                                if (!matchesFilter) return@firstOrNull false

                                val markerX = inc.longitudeRatio * width
                                val markerY = inc.latitudeRatio * height
                                val dist = sqrt((offset.x - markerX) * (offset.x - markerX) + (offset.y - markerY) * (offset.y - markerY))
                                dist < 45f // collision radius
                            }

                            if (clickedIncident != null) {
                                selectedIncidentRef = clickedIncident
                                selectedResourceRef = null
                                activeMarkerInfo = clickedIncident.title to "${clickedIncident.locationName} • ${clickedIncident.severity}"
                                return@detectTapGestures
                            }

                            // Check collision with resources
                            val clickedResource = resources.firstOrNull { res ->
                                if (selectedFilter != MapLayerFilter.ALL && selectedFilter != MapLayerFilter.RESOURCES) return@firstOrNull false
                                val markerX = 0.40f * width
                                val markerY = 0.35f * height
                                val dist = sqrt((offset.x - markerX) * (offset.x - markerX) + (offset.y - markerY) * (offset.y - markerY))
                                dist < 45f
                            }

                            if (clickedResource != null) {
                                selectedResourceRef = clickedResource
                                selectedIncidentRef = null
                                activeMarkerInfo = clickedResource.donorName to "Surplus: ${clickedResource.quantity} • ${clickedResource.expiresAt}"
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw high-tech tactical grid
                val gridSpacing = 40.dp.toPx()
                var x = 0f
                while (x < canvasWidth) {
                    drawLine(
                        color = CommandBorder.copy(alpha = 0.25f),
                        start = Offset(x, 0f),
                        end = Offset(x, canvasHeight),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }
                var y = 0f
                while (y < canvasHeight) {
                    drawLine(
                        color = CommandBorder.copy(alpha = 0.25f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }

                // Draw river corridor (geography)
                val riverPath = Path().apply {
                    moveTo(0f, canvasHeight * 0.72f)
                    cubicTo(
                        canvasWidth * 0.3f, canvasHeight * 0.65f,
                        canvasWidth * 0.6f, canvasHeight * 0.85f,
                        canvasWidth, canvasHeight * 0.78f
                    )
                }
                drawPath(
                    path = riverPath,
                    color = Color(0xFF0F3B5F).copy(alpha = 0.4f),
                    style = Stroke(width = 16.dp.toPx())
                )

                // Draw major highway corridors
                drawLine(
                    color = CommandSurfaceHighlight,
                    start = Offset(canvasWidth * 0.15f, 0f),
                    end = Offset(canvasWidth * 0.85f, canvasHeight),
                    strokeWidth = 4.dp.toPx()
                )
                drawLine(
                    color = CommandSurfaceHighlight,
                    start = Offset(0f, canvasHeight * 0.45f),
                    end = Offset(canvasWidth, canvasHeight * 0.45f),
                    strokeWidth = 4.dp.toPx()
                )

                // Draw Metro line
                drawLine(
                    color = IndigoIntelligence.copy(alpha = 0.4f),
                    start = Offset(canvasWidth * 0.2f, canvasHeight * 0.15f),
                    end = Offset(canvasWidth * 0.75f, canvasHeight * 0.75f),
                    strokeWidth = 2.dp.toPx()
                )

                // Draw radar scanning rings around city center
                drawCircle(
                    color = CyanAccent.copy(alpha = 0.08f),
                    radius = canvasWidth * 0.35f,
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Draw Incidents as pulsing tactical nodes
                incidents.forEach { incident ->
                    val show = when (selectedFilter) {
                        MapLayerFilter.ALL -> true
                        MapLayerFilter.TRAFFIC -> incident.type == "TRAFFIC"
                        MapLayerFilter.AIR_QUALITY -> incident.type == "AIR_QUALITY"
                        MapLayerFilter.WEATHER -> incident.type == "WEATHER"
                        MapLayerFilter.SECURITY -> incident.type == "SECURITY"
                        MapLayerFilter.RESOURCES -> incident.type == "RESOURCE"
                        else -> false
                    }
                    if (show) {
                        val nodeX = incident.longitudeRatio * canvasWidth
                        val nodeY = incident.latitudeRatio * canvasHeight
                        val nodeColor = when (incident.severity) {
                            "CRITICAL" -> AlertCritical
                            "HIGH" -> AlertHigh
                            "MEDIUM" -> AlertMedium
                            else -> CyanAccent
                        }

                        // Outer pulse ring
                        drawCircle(
                            color = nodeColor.copy(alpha = 0.25f),
                            radius = 14.dp.toPx(),
                            center = Offset(nodeX, nodeY)
                        )
                        // Inner marker dot
                        drawCircle(
                            color = nodeColor,
                            radius = 6.dp.toPx(),
                            center = Offset(nodeX, nodeY)
                        )
                    }
                }

                // Draw Shelters as Safe Green Squares
                if (selectedFilter == MapLayerFilter.ALL || selectedFilter == MapLayerFilter.SHELTERS) {
                    val shelterPositions = listOf(
                        Offset(canvasWidth * 0.32f, canvasHeight * 0.48f),
                        Offset(canvasWidth * 0.70f, canvasHeight * 0.38f),
                        Offset(canvasWidth * 0.48f, canvasHeight * 0.82f)
                    )
                    shelterPositions.forEach { pos ->
                        drawCircle(
                            color = StatusSafe.copy(alpha = 0.3f),
                            radius = 12.dp.toPx(),
                            center = pos
                        )
                        drawCircle(
                            color = StatusSafe,
                            radius = 5.dp.toPx(),
                            center = pos
                        )
                    }
                }
            }

            // Legend Overlay (Top Left inside map)
            Surface(
                color = CommandSurfaceDark.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AlertCritical))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Critical", fontSize = 10.sp, color = TextPrimaryDark)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AlertHigh))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("High", fontSize = 10.sp, color = TextPrimaryDark)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusSafe))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Shelter", fontSize = 10.sp, color = TextPrimaryDark)
                    }
                }
            }

            // Bottom Selected Marker Card Overlay
            activeMarkerInfo?.let { (title, subtitle) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CommandCardDark.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark,
                                maxLines = 1
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryDark,
                                maxLines = 1
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            selectedIncidentRef?.let { inc ->
                                Button(
                                    onClick = { onIncidentSelected(inc) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Inspect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            selectedResourceRef?.let { res ->
                                Button(
                                    onClick = { onAcceptResource(res) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPulse, contentColor = Color.Black),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Rescue Route", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
