package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.ScreenNav
import com.example.ui.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandTopBar(
    currentRole: UserRole,
    onRoleChangeRequest: () -> Unit,
    unreadAlertCount: Int,
    onAlertsClick: () -> Unit,
    isLockdownActive: Boolean,
    onEmergencyLockClick: () -> Unit
) {
    Surface(
        color = CommandSurfaceDark,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand and system indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(CyanAccent, IndigoIntelligence)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "SmartShield Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SMARTSHIELD",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = TextPrimaryDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CITY",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = CyanAccent
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isLockdownActive) AlertCritical else StatusSafe)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLockdownActive) "DEFENSE LOCKDOWN" else "LIVE COMMAND ONLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (isLockdownActive) AlertCritical else StatusSafe
                            )
                        }
                    }
                }

                // Action buttons: Emergency Lockdown, Role Switcher, Alert Bell
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentRole == UserRole.ADMIN || currentRole == UserRole.SECURITY_STAFF) {
                        IconButton(
                            onClick = onEmergencyLockClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isLockdownActive) AlertCritical else CommandCardDark)
                                .testTag("emergency_lock_button")
                        ) {
                            Icon(
                                imageVector = if (isLockdownActive) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Emergency Lockdown",
                                tint = if (isLockdownActive) Color.White else AlertCritical,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Role Chip Switcher
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CommandCardDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                        modifier = Modifier
                            .clickable { onRoleChangeRequest() }
                            .testTag("role_switcher_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = currentRole.badge,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch Role",
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Alert Notification Bell
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CommandCardDark)
                            .clickable { onAlertsClick() }
                            .testTag("alert_bell_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alerts",
                            tint = if (unreadAlertCount > 0) AlertHigh else TextSecondaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                        if (unreadAlertCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(AlertCritical),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unreadAlertCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = CommandBorder, thickness = 1.dp)
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    type: String, // CRITICAL, HIGH, MEDIUM, LOW, SAFE, ACTIVE, RESOLVED, SECURE, VULNERABLE
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (type.uppercase()) {
        "CRITICAL" -> AlertCritical.copy(alpha = 0.2f) to AlertCritical
        "HIGH" -> AlertHigh.copy(alpha = 0.2f) to AlertHigh
        "MEDIUM" -> AlertMedium.copy(alpha = 0.2f) to AlertMedium
        "LOW", "INFO" -> AlertLow.copy(alpha = 0.2f) to AlertLow
        "SAFE", "RESOLVED", "SECURE", "DELIVERED" -> StatusSafe.copy(alpha = 0.2f) to StatusSafe
        "VULNERABLE" -> AlertCritical.copy(alpha = 0.2f) to AlertCritical
        "IN_TRANSIT", "RESPONDING" -> IndigoIntelligence.copy(alpha = 0.2f) to IndigoIntelligence
        else -> CommandSurfaceHighlight to TextSecondaryDark
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun MetricKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CommandCardDark),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondaryDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp
                ),
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RoleSelectionDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommandSurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.People, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select Persona & Dashboard",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "SmartShield adapts permissions and tools to each persona role:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
                UserRole.values().forEach { role ->
                    val isSelected = role == currentRole
                    Surface(
                        color = if (isSelected) CommandSurfaceHighlight else CommandCardDark,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) CyanAccent else CommandBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onRoleSelected(role)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = role.badge,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) CyanAccent else TextPrimaryDark
                                )
                                Text(
                                    text = when (role) {
                                        UserRole.ADMIN -> "Full access across all 4 pillars, audit logs & emergency response"
                                        UserRole.CITY_STAFF -> "Civic monitoring, live map, incidents & AI correlations"
                                        UserRole.NGO -> "Food surplus rescue, recipient matching & routing dispatch"
                                        UserRole.SECURITY_STAFF -> "SentinelAPI scanner, vulnerability audits & threat protection"
                                        UserRole.RESIDENT -> "Public safety advisory, weather, air quality & local alerts"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryDark
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Active",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = CyanAccent)
            }
        }
    )
}
