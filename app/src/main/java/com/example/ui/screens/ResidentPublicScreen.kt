package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.ShelterEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ResidentPublicScreen(
    alerts: List<AlertItem>,
    shelters: List<ShelterEntity>,
    onReportIssueClick: () -> Unit,
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
                    Text(
                        text = "Resident Public Portal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Live public safety advisory & community shelters",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }

                Button(
                    onClick = onReportIssueClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("report_issue_button")
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report Issue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Environmental Conditions Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = AlertHigh)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("City Weather & Air Health", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }
                        StatusBadge(text = "AQI: 142 Moderate", type = "MEDIUM")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("28°C", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimaryDark)
                            Text("Temperature", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("64%", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimaryDark)
                            Text("Humidity", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("12 km/h", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimaryDark)
                            Text("Wind Speed", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Good", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StatusSafe)
                            Text("Water Supply", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }
        }

        // Public Community Safety Bulletins
        item {
            Text(
                text = "Community Safety Bulletins",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        items(alerts.filter { it.category != "SECURITY" }) { alert ->
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
                        StatusBadge(text = alert.severity, type = alert.severity)
                        Text(alert.timeAgo, fontSize = 11.sp, color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(alert.title, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Text(alert.message, fontSize = 12.sp, color = TextSecondaryDark)
                }
            }
        }

        // Designated Community Safe Shelters
        item {
            Text(
                text = "Emergency Community Shelters",
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
                    Text(shelter.name, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Text("📍 ${shelter.address}", fontSize = 12.sp, color = CyanAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Available Beds: ${shelter.capacity - shelter.currentOccupancy} free (${shelter.capacity} capacity)", fontSize = 11.sp, color = TextSecondaryDark)
                    Text("Accepted Items: ${shelter.acceptedFoodTypes}", fontSize = 11.sp, color = StatusSafe)
                }
            }
        }
    }
}
