package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.ResourceOffer
import com.example.data.ShelterEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ResourceRescueScreen(
    resources: List<ResourceOffer>,
    shelters: List<ShelterEntity>,
    onAddSurplusClick: () -> Unit,
    onAcceptMatchClick: (ResourceOffer) -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Recycling, contentDescription = null, tint = TealPulse)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Surplus-to-Shelter",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                    }
                    Text(
                        text = "Smart food rescue & shelter matching system",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }

                Button(
                    onClick = onAddSurplusClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPulse, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_surplus_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Surplus", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Metrics Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${resources.size}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = TealPulse)
                        Text("Available", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("5", fontWeight = FontWeight.Black, fontSize = 20.sp, color = AlertHigh)
                        Text("Urgent", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("42", fontWeight = FontWeight.Black, fontSize = 20.sp, color = StatusSafe)
                        Text("Delivered", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("1,240 kg", fontWeight = FontWeight.Black, fontSize = 20.sp, color = CyanAccent)
                        Text("Rescued", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                }
            }
        }

        // Active Surplus Offers List
        item {
            Text(
                text = "Active Surplus Offers",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        items(resources) { resource ->
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
                            text = resource.donorName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        StatusBadge(text = resource.status, type = resource.status)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "🍱 ${resource.quantity} • ${resource.foodType}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TealPulse
                    )

                    Text(
                        text = "📍 ${resource.pickupLocation} • ⏳ Expires: ${resource.expiresAt}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // AI Best Match Card
                    Surface(
                        color = CommandSurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealPulse.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = TealPulse, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Best Recipient: ${resource.bestMatchShelter}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimaryDark
                                    )
                                }
                                StatusBadge(text = "${resource.matchScore}% Match", type = "SAFE")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Distance: ${resource.distanceKm} km • Urgency: HIGH",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (resource.status != "DELIVERED") {
                        Button(
                            onClick = { onAcceptMatchClick(resource) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("accept_match_button")
                        ) {
                            Text("Accept Match & Dispatch Route", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Shelter Network Overview
        item {
            Text(
                text = "Partner Shelters & Capacity",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        items(shelters) { shelter ->
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
                            text = shelter.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryDark
                        )
                        StatusBadge(text = "Need: ${shelter.needLevel}", type = shelter.needLevel)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Occupancy: ${shelter.currentOccupancy} / ${shelter.capacity} beds (${(shelter.currentOccupancy * 100) / shelter.capacity}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { shelter.currentOccupancy.toFloat() / shelter.capacity },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (shelter.currentOccupancy > shelter.capacity * 0.8) AlertHigh else StatusSafe,
                        trackColor = CommandSurfaceHighlight
                    )
                }
            }
        }
    }
}
