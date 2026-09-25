package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CivicIncident
import com.example.data.ResourceOffer
import com.example.data.VulnerabilityFinding
import com.example.ui.ScanProgressState
import com.example.ui.ScreenNav
import com.example.ui.SmartShieldViewModel
import com.example.ui.UserRole
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: SmartShieldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartShieldTheme {
                val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val bannerMsg by viewModel.globalBannerMessage.collectAsStateWithLifecycle()

                // State from DB
                val incidents by viewModel.incidents.collectAsStateWithLifecycle()
                val resources by viewModel.resources.collectAsStateWithLifecycle()
                val shelters by viewModel.shelters.collectAsStateWithLifecycle()
                val dispatchTasks by viewModel.dispatchTasks.collectAsStateWithLifecycle()
                val apiTargets by viewModel.apiTargets.collectAsStateWithLifecycle()
                val vulnerabilities by viewModel.vulnerabilities.collectAsStateWithLifecycle()
                val securityEvents by viewModel.securityEvents.collectAsStateWithLifecycle()
                val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
                val alerts by viewModel.alerts.collectAsStateWithLifecycle()

                // Threat & Security States
                val isSystemLocked by viewModel.isSystemLocked.collectAsStateWithLifecycle()
                val failedAttempts by viewModel.failedAttempts.collectAsStateWithLifecycle()
                val usbConnected by viewModel.usbKeyConnected.collectAsStateWithLifecycle()
                val activeThreats by viewModel.activeThreatCount.collectAsStateWithLifecycle()
                val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()

                // Dialog states
                var showRoleDialog by remember { mutableStateOf(false) }
                var showAddSurplusDialog by remember { mutableStateOf(false) }
                var showMoreNavSheet by remember { mutableStateOf(false) }

                val selectedIncident by viewModel.selectedIncident.collectAsStateWithLifecycle()
                val selectedVulnerability by viewModel.selectedVulnerability.collectAsStateWithLifecycle()

                // Back navigation handling
                BackHandler(enabled = currentScreen != ScreenNav.DASHBOARD) {
                    viewModel.navigateTo(ScreenNav.DASHBOARD)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CommandBackgroundDark),
                    containerColor = CommandBackgroundDark,
                    topBar = {
                        CommandTopBar(
                            currentRole = currentRole,
                            onRoleChangeRequest = { showRoleDialog = true },
                            unreadAlertCount = alerts.count { !it.isAcknowledged },
                            onAlertsClick = { viewModel.navigateTo(ScreenNav.ALERTS) },
                            isLockdownActive = isSystemLocked,
                            onEmergencyLockClick = { viewModel.toggleSystemLockdown() }
                        )
                    },
                    bottomBar = {
                        CommandBottomNavigation(
                            currentScreen = currentScreen,
                            currentRole = currentRole,
                            onSelectScreen = { screen ->
                                if (screen == ScreenNav.SETTINGS) {
                                    showMoreNavSheet = true
                                } else {
                                    viewModel.navigateTo(screen)
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Main Screen Routing
                        when (currentScreen) {
                            ScreenNav.DASHBOARD -> {
                                if (currentRole == UserRole.RESIDENT) {
                                    ResidentPublicScreen(
                                        alerts = alerts,
                                        shelters = shelters,
                                        onReportIssueClick = { viewModel.simulateLiveCityAnomaly() }
                                    )
                                } else {
                                    DashboardScreen(
                                        currentRole = currentRole,
                                        incidents = incidents,
                                        resources = resources,
                                        alerts = alerts,
                                        vulnerabilities = vulnerabilities,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onIncidentClick = { viewModel.selectIncident(it) },
                                        onSimulateAnomaly = { viewModel.simulateLiveCityAnomaly() },
                                        onAddSurplusClick = { showAddSurplusDialog = true },
                                        onTriggerScanClick = { viewModel.navigateTo(ScreenNav.API_SCANNER) },
                                        isLockdownActive = isSystemLocked
                                    )
                                }
                            }

                            ScreenNav.CIVIC_MAP -> {
                                CivicMapScreen(
                                    incidents = incidents,
                                    resources = resources,
                                    shelters = shelters,
                                    onIncidentSelected = { viewModel.selectIncident(it) },
                                    onAcceptResource = { viewModel.acceptMatchAndDispatch(it) }
                                )
                            }

                            ScreenNav.INCIDENTS -> {
                                IncidentsScreen(
                                    incidents = incidents,
                                    onIncidentClick = { viewModel.selectIncident(it) },
                                    onSimulateAnomaly = { viewModel.simulateLiveCityAnomaly() }
                                )
                            }

                            ScreenNav.AI_INSIGHTS -> {
                                AiInsightsScreen(incidents = incidents)
                            }

                            ScreenNav.RESOURCES -> {
                                ResourceRescueScreen(
                                    resources = resources,
                                    shelters = shelters,
                                    onAddSurplusClick = { showAddSurplusDialog = true },
                                    onAcceptMatchClick = { viewModel.acceptMatchAndDispatch(it) }
                                )
                            }

                            ScreenNav.DISPATCH -> {
                                DispatchScreen(
                                    tasks = dispatchTasks,
                                    onAdvanceStatus = { id, st -> viewModel.advanceDispatchStatus(id, st) }
                                )
                            }

                            ScreenNav.SECURITY -> {
                                SecurityCenterScreen(
                                    apiTargets = apiTargets,
                                    vulnerabilities = vulnerabilities,
                                    securityEvents = securityEvents,
                                    isSystemLocked = isSystemLocked,
                                    failedAttempts = failedAttempts,
                                    usbConnected = usbConnected,
                                    activeThreats = activeThreats,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onVulnerabilityClick = { viewModel.selectVulnerability(it) },
                                    onToggleLockdown = { viewModel.toggleSystemLockdown() },
                                    onToggleUsb = { viewModel.toggleUsbKey() },
                                    onTriggerBackup = { viewModel.triggerBackupSnapshot() },
                                    onBlockIp = { viewModel.blockSuspiciousIp() }
                                )
                            }

                            ScreenNav.API_SCANNER -> {
                                ApiScannerScreen(
                                    scanProgress = scanProgress,
                                    onStartScan = { name, url -> viewModel.startApiScan(name, url) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }

                            ScreenNav.ALERTS -> {
                                AlertsScreen(
                                    alerts = alerts,
                                    onAcknowledgeAlert = { viewModel.acknowledgeAlert(it) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }

                            ScreenNav.ANALYTICS -> {
                                AnalyticsScreen()
                            }

                            ScreenNav.REPORTS -> {
                                ReportsScreen()
                            }

                            ScreenNav.AUDIT_LOGS -> {
                                AuditLogsScreen(auditLogs = auditLogs)
                            }

                            ScreenNav.SETTINGS -> {
                                SettingsScreen(
                                    currentRole = currentRole,
                                    onRoleChangeRequest = { showRoleDialog = true },
                                    usbKeyConnected = usbConnected,
                                    onToggleUsb = { viewModel.toggleUsbKey() }
                                )
                            }
                        }

                        // Floating Banner Toast Notification
                        bannerMsg?.let { msg ->
                            Surface(
                                color = CommandSurfaceHighlight,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent),
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                                    .fillMaxWidth()
                                    .clickable { viewModel.clearBanner() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextPrimaryDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = TextSecondaryDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Persona Role Selection Dialog
                if (showRoleDialog) {
                    RoleSelectionDialog(
                        currentRole = currentRole,
                        onRoleSelected = { viewModel.setRole(it) },
                        onDismiss = { showRoleDialog = false }
                    )
                }

                // Incident Inspector Dialog
                selectedIncident?.let { inc ->
                    IncidentDetailDialog(
                        incident = inc,
                        onDismiss = { viewModel.selectIncident(null) },
                        onAssignTeam = { id, team -> viewModel.assignTeamToIncident(id, team) },
                        onResolve = { id -> viewModel.resolveIncident(id) }
                    )
                }

                // Vulnerability Inspector Dialog
                selectedVulnerability?.let { vuln ->
                    VulnerabilityDetailDialog(
                        vuln = vuln,
                        onDismiss = { viewModel.selectVulnerability(null) },
                        onResolve = { id -> viewModel.markVulnerabilityResolved(id) }
                    )
                }

                // Add Surplus Offer Dialog
                if (showAddSurplusDialog) {
                    AddSurplusDialog(
                        onDismiss = { showAddSurplusDialog = false },
                        onSubmit = { donor, type, qty, loc, exp ->
                            viewModel.createSurplusOffer(donor, type, qty, loc, exp)
                        }
                    )
                }

                // "More..." Navigation Grid Modal
                if (showMoreNavSheet) {
                    MoreNavigationSheet(
                        onNavigate = { screen ->
                            showMoreNavSheet = false
                            viewModel.navigateTo(screen)
                        },
                        onDismiss = { showMoreNavSheet = false }
                    )
                }
            }
        }
    }
}

data class NavItem(val screen: ScreenNav, val label: String, val icon: ImageVector)

@Composable
fun CommandBottomNavigation(
    currentScreen: ScreenNav,
    currentRole: UserRole,
    onSelectScreen: (ScreenNav) -> Unit
) {
    val items = when (currentRole) {
        UserRole.SECURITY_STAFF -> listOf(
            NavItem(ScreenNav.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
            NavItem(ScreenNav.SECURITY, "Security", Icons.Default.Security),
            NavItem(ScreenNav.API_SCANNER, "Scanner", Icons.Default.Radar),
            NavItem(ScreenNav.AUDIT_LOGS, "Audit", Icons.Default.History),
            NavItem(ScreenNav.SETTINGS, "More", Icons.Default.Menu)
        )
        UserRole.NGO -> listOf(
            NavItem(ScreenNav.DASHBOARD, "Overview", Icons.Default.Dashboard),
            NavItem(ScreenNav.RESOURCES, "Rescue", Icons.Default.Recycling),
            NavItem(ScreenNav.DISPATCH, "Dispatch", Icons.Default.LocalShipping),
            NavItem(ScreenNav.CIVIC_MAP, "Map", Icons.Default.Map),
            NavItem(ScreenNav.SETTINGS, "More", Icons.Default.Menu)
        )
        else -> listOf(
            NavItem(ScreenNav.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
            NavItem(ScreenNav.CIVIC_MAP, "Map", Icons.Default.Map),
            NavItem(ScreenNav.INCIDENTS, "Incidents", Icons.Default.Warning),
            NavItem(ScreenNav.RESOURCES, "Surplus", Icons.Default.Recycling),
            NavItem(ScreenNav.SECURITY, "Security", Icons.Default.Security),
            NavItem(ScreenNav.SETTINGS, "More", Icons.Default.Menu)
        )
    }

    NavigationBar(
        containerColor = CommandSurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier.height(72.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectScreen(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) CyanAccent else TextSecondaryDark
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CyanAccent else TextSecondaryDark
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = CyanAccent.copy(alpha = 0.15f),
                    selectedIconColor = CyanAccent,
                    selectedTextColor = CyanAccent,
                    unselectedIconColor = TextSecondaryDark,
                    unselectedTextColor = TextSecondaryDark
                ),
                modifier = Modifier.testTag("nav_item_${item.label.lowercase()}")
            )
        }
    }
}

@Composable
fun MoreNavigationSheet(
    onNavigate: (ScreenNav) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommandSurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Apps, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Command Center Hub",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            }
        },
        text = {
            val allModules = listOf(
                NavItem(ScreenNav.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
                NavItem(ScreenNav.CIVIC_MAP, "Live Tactical Map", Icons.Default.Map),
                NavItem(ScreenNav.INCIDENTS, "Civic Incidents", Icons.Default.Warning),
                NavItem(ScreenNav.AI_INSIGHTS, "CityPulse AI Engine", Icons.Default.Psychology),
                NavItem(ScreenNav.ANALYTICS, "Charts & Analytics", Icons.Default.BarChart),
                NavItem(ScreenNav.RESOURCES, "Resource Rescue", Icons.Default.Recycling),
                NavItem(ScreenNav.DISPATCH, "Smart Routing", Icons.Default.LocalShipping),
                NavItem(ScreenNav.SECURITY, "Sentinel SecOps", Icons.Default.Security),
                NavItem(ScreenNav.API_SCANNER, "API Scanner", Icons.Default.Radar),
                NavItem(ScreenNav.ALERTS, "Alert Center", Icons.Default.Notifications),
                NavItem(ScreenNav.REPORTS, "Reports & Export", Icons.Default.Assessment),
                NavItem(ScreenNav.AUDIT_LOGS, "Audit Trail", Icons.Default.History),
                NavItem(ScreenNav.SETTINGS, "System Settings", Icons.Default.Settings)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(allModules) { module ->
                    Surface(
                        color = CommandCardDark,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(module.screen) }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(module.icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(module.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
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
