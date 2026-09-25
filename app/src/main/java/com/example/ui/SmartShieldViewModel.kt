package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class UserRole(val displayName: String, val badge: String) {
    ADMIN("Administrator", "👑 Chief Command"),
    CITY_STAFF("City Operator", "🏙️ Civic Operations"),
    NGO("NGO Coordinator", "🏢 Resource Rescue"),
    SECURITY_STAFF("Security Lead", "🔐 Sentinel SecOps"),
    RESIDENT("Resident", "👤 Public Portal")
}

enum class ScreenNav(val title: String, val section: String) {
    DASHBOARD("Dashboard", "OVERVIEW"),
    CIVIC_MAP("Live Map", "CITY"),
    INCIDENTS("Incidents", "CITY"),
    AI_INSIGHTS("AI Insights", "CITY"),
    ANALYTICS("Analytics", "CITY"),
    RESOURCES("Resource Rescue", "RESOURCE"),
    DISPATCH("Dispatch & Routes", "RESOURCE"),
    SECURITY("Security Center", "SECURITY"),
    API_SCANNER("API Scanner", "SECURITY"),
    ALERTS("Alert Center", "SYSTEM"),
    REPORTS("Reports", "SYSTEM"),
    AUDIT_LOGS("Audit Logs", "SYSTEM"),
    SETTINGS("Settings", "SYSTEM")
}

data class ScanProgressState(
    val isScanning: Boolean = false,
    val currentStep: String = "",
    val progress: Float = 0f,
    val scannedEndpoints: Int = 0,
    val totalEndpoints: Int = 58,
    val completedSummary: String? = null
)

class SmartShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SmartShieldDatabase.getDatabase(application, viewModelScope)
    val repository = SmartShieldRepository(database.smartShieldDao())

    // UI States
    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentScreen = MutableStateFlow(ScreenNav.DASHBOARD)
    val currentScreen: StateFlow<ScreenNav> = _currentScreen.asStateFlow()

    private val _selectedIncident = MutableStateFlow<CivicIncident?>(null)
    val selectedIncident: StateFlow<CivicIncident?> = _selectedIncident.asStateFlow()

    private val _selectedVulnerability = MutableStateFlow<VulnerabilityFinding?>(null)
    val selectedVulnerability: StateFlow<VulnerabilityFinding?> = _selectedVulnerability.asStateFlow()

    private val _scanProgress = MutableStateFlow(ScanProgressState())
    val scanProgress: StateFlow<ScanProgressState> = _scanProgress.asStateFlow()

    // Threat Protection Status (Deadman's Drive)
    private val _isSystemLocked = MutableStateFlow(false)
    val isSystemLocked: StateFlow<Boolean> = _isSystemLocked.asStateFlow()

    private val _failedAttempts = MutableStateFlow(2)
    val failedAttempts: StateFlow<Int> = _failedAttempts.asStateFlow()

    private val _usbKeyConnected = MutableStateFlow(true)
    val usbKeyConnected: StateFlow<Boolean> = _usbKeyConnected.asStateFlow()

    private val _activeThreatCount = MutableStateFlow(1)
    val activeThreatCount: StateFlow<Int> = _activeThreatCount.asStateFlow()

    private val _globalBannerMessage = MutableStateFlow<String?>(null)
    val globalBannerMessage: StateFlow<String?> = _globalBannerMessage.asStateFlow()

    // Database flows
    val incidents = repository.allIncidents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val resources = repository.allResources.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val shelters = repository.allShelters.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val dispatchTasks = repository.allDispatchTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val apiTargets = repository.allApiTargets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val vulnerabilities = repository.allVulnerabilities.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val securityEvents = repository.allSecurityEvents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val auditLogs = repository.allAuditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val alerts = repository.allAlerts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setRole(role: UserRole) {
        _currentRole.value = role
        // Redirect to appropriate starting screen for role if outside permissions
        when (role) {
            UserRole.RESIDENT -> {
                if (_currentScreen.value in listOf(ScreenNav.SECURITY, ScreenNav.API_SCANNER, ScreenNav.AUDIT_LOGS)) {
                    _currentScreen.value = ScreenNav.DASHBOARD
                }
            }
            UserRole.NGO -> {
                if (_currentScreen.value in listOf(ScreenNav.SECURITY, ScreenNav.API_SCANNER)) {
                    _currentScreen.value = ScreenNav.RESOURCES
                }
            }
            UserRole.SECURITY_STAFF -> {
                _currentScreen.value = ScreenNav.SECURITY
            }
            else -> {}
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.logAudit(
                user = role.displayName,
                role = role.name,
                action = "Switched Persona Session",
                targetResource = "Command Console",
                result = "SUCCESS"
            )
        }
    }

    fun navigateTo(screen: ScreenNav) {
        _currentScreen.value = screen
    }

    fun selectIncident(incident: CivicIncident?) {
        _selectedIncident.value = incident
    }

    fun selectVulnerability(vuln: VulnerabilityFinding?) {
        _selectedVulnerability.value = vuln
    }

    fun clearBanner() {
        _globalBannerMessage.value = null
    }

    // Incident Actions
    fun assignTeamToIncident(incidentId: Int, teamName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIncidentStatus(incidentId, "RESPONDING", teamName)
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Assigned Emergency Team",
                targetResource = "Incident #$incidentId -> $teamName",
                result = "SUCCESS"
            )
            _globalBannerMessage.value = "Team '$teamName' deployed to incident."
        }
    }

    fun resolveIncident(incidentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIncidentStatus(incidentId, "RESOLVED", null)
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Resolved Incident",
                targetResource = "Incident #$incidentId",
                result = "SUCCESS"
            )
            _globalBannerMessage.value = "Incident #$incidentId marked as resolved."
        }
    }

    // Resource Rescue Actions
    fun createSurplusOffer(donorName: String, foodType: String, quantity: String, location: String, expiry: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val targetShelter = shelters.value.firstOrNull()?.name ?: "Hope Haven Shelter"
            val newResource = ResourceOffer(
                donorName = donorName,
                foodType = foodType,
                quantity = quantity,
                pickupLocation = location,
                expiresAt = expiry,
                status = "AVAILABLE",
                bestMatchShelter = targetShelter,
                matchScore = 95,
                distanceKm = 2.4f
            )
            val id = repository.addResource(newResource)
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Published Surplus Food Offer",
                targetResource = "$donorName ($quantity)",
                result = "SUCCESS"
            )
            _globalBannerMessage.value = "Surplus posted! AI Match Score 95% calculated."
        }
    }

    fun acceptMatchAndDispatch(resource: ResourceOffer, driver: String = "Rahul Sharma") {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateResourceStatus(resource.id, "DISPATCHED")
            val task = DispatchTask(
                taskId = "DISP-#${System.currentTimeMillis() % 10000}",
                donorName = resource.donorName,
                recipientName = resource.bestMatchShelter,
                driverName = driver,
                distanceKm = resource.distanceKm,
                etaMinutes = (resource.distanceKm * 3.5).toInt().coerceAtLeast(8),
                status = "ASSIGNED",
                routeNotes = "Optimal green corridor selected. Bypass congested Sector 2."
            )
            repository.createDispatchTask(task)
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Accepted Match & Created Dispatch",
                targetResource = "${resource.donorName} -> ${resource.bestMatchShelter}",
                result = "SUCCESS"
            )
            _globalBannerMessage.value = "Dispatch task created! Assigned to driver $driver."
            _currentScreen.value = ScreenNav.DISPATCH
        }
    }

    fun advanceDispatchStatus(taskId: Int, currentStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val nextStatus = when (currentStatus) {
                "ASSIGNED" -> "PICKUP_STARTED"
                "PICKUP_STARTED" -> "IN_TRANSIT"
                "IN_TRANSIT" -> "DELIVERED"
                else -> "DELIVERED"
            }
            repository.updateDispatchStatus(taskId, nextStatus)
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Updated Dispatch Step",
                targetResource = "Dispatch #$taskId to $nextStatus",
                result = "SUCCESS"
            )
            _globalBannerMessage.value = "Delivery status advanced to: $nextStatus"
        }
    }

    // Security Center & API Scanner (SentinelAPI)
    fun startApiScan(apiName: String, swaggerUrl: String) {
        viewModelScope.launch {
            _scanProgress.value = ScanProgressState(
                isScanning = true,
                currentStep = "Connecting to $apiName OpenAPI schema...",
                progress = 0.1f,
                scannedEndpoints = 0
            )
            delay(700)
            _scanProgress.value = _scanProgress.value.copy(
                currentStep = "Testing Broken Object Level Auth (BOLA / BFLA)...",
                progress = 0.35f,
                scannedEndpoints = 18
            )
            delay(800)
            _scanProgress.value = _scanProgress.value.copy(
                currentStep = "Auditing Token Bucket & Rate Limiting...",
                progress = 0.65f,
                scannedEndpoints = 36
            )
            delay(700)
            _scanProgress.value = _scanProgress.value.copy(
                currentStep = "Scanning Excessive Data Exposure & PII leakage...",
                progress = 0.9f,
                scannedEndpoints = 52
            )
            delay(600)
            _scanProgress.value = _scanProgress.value.copy(
                isScanning = false,
                currentStep = "Scan Complete. Report generated!",
                progress = 1.0f,
                scannedEndpoints = 58,
                completedSummary = "Audited 58 endpoints. Found 1 Critical BOLA, 1 Rate-Limit weakness, 42 Secure."
            )

            // Insert discovered vulnerability
            repository.addVulnerability(
                VulnerabilityFinding(
                    apiTargetId = 1,
                    apiName = apiName,
                    endpoint = "/api/v2/vehicles/diagnostics",
                    httpMethod = "GET",
                    vulnerabilityType = "Broken Function Level Authorization (BFLA)",
                    severity = "CRITICAL",
                    description = "Administrative telemetry diagnostics accessible with ordinary citizen token.",
                    evidence = "HTTP 200 returned full internal CAN-bus telemetry on unprivileged bearer header.",
                    impact = "Allows unauthorized access to hardware diagnostics and remote shutoff controls.",
                    recommendation = "Require Role:ADMIN_FLEET claim verification in authorization filter.",
                    status = "OPEN"
                )
            )

            repository.addAlert(
                AlertItem(
                    title = "New Critical Vulnerability in $apiName",
                    message = "BFLA detected on /api/v2/vehicles/diagnostics during automated scan.",
                    category = "SECURITY",
                    severity = "CRITICAL",
                    timeAgo = "Just now",
                    targetScreen = "SECURITY"
                )
            )

            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Completed SentinelAPI Automated Scan",
                targetResource = apiName,
                result = "FLAGGED"
            )
        }
    }

    fun markVulnerabilityResolved(vulnId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resolveVulnerability(vulnId)
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Marked Vulnerability Resolved",
                targetResource = "Vulnerability Finding #$vulnId",
                result = "SUCCESS"
            )
            _globalBannerMessage.value = "Finding marked as resolved and logged to audit."
        }
    }

    // Threat Protection (Deadman's Drive)
    fun toggleSystemLockdown() {
        val newLock = !_isSystemLocked.value
        _isSystemLocked.value = newLock
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordSecurityEvent(
                SecurityEvent(
                    title = if (newLock) "EMERGENCY SYSTEM LOCKDOWN ENGAGED" else "System Lockdown Disengaged",
                    eventType = "SESSION_LOCK",
                    severity = if (newLock) "CRITICAL" else "INFO",
                    details = "Initiated by ${_currentRole.value.displayName}. Administrative ports isolated.",
                    timestampStr = "Just now"
                )
            )
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = if (newLock) "TRIGGERED LOCKDOWN" else "RELEASED LOCKDOWN",
                targetResource = "City Perimeter Security Controller",
                result = if (newLock) "DENIED" else "SUCCESS"
            )
            _globalBannerMessage.value = if (newLock) "⚠️ LOCKDOWN ACTIVE: Non-essential traffic halted." else "System normal operations resumed."
        }
    }

    fun toggleUsbKey() {
        val newState = !_usbKeyConnected.value
        _usbKeyConnected.value = newState
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordSecurityEvent(
                SecurityEvent(
                    title = if (newState) "Hardware Security Token Verified" else "Hardware Key Detached",
                    eventType = "USB_VERIFY",
                    severity = if (newState) "INFO" else "HIGH",
                    details = if (newState) "FIDO2 Key validated via port 4." else "Physical key detached. Dual-auth enforced.",
                    timestampStr = "Just now"
                )
            )
            _globalBannerMessage.value = if (newState) "Hardware Security Token Online." else "Warning: Hardware token detached."
        }
    }

    fun triggerBackupSnapshot() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordSecurityEvent(
                SecurityEvent(
                    title = "Emergency Encrypted Snapshot Created",
                    eventType = "BACKUP_TRIGGER",
                    severity = "INFO",
                    details = "Full encrypted state backed up to air-gapped immutable storage.",
                    timestampStr = "Just now"
                )
            )
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Executed Emergency Cold Backup",
                targetResource = "State Snapshot Vault",
                result = "SUCCESS"
            )
            _globalBannerMessage.value = "Cold encrypted backup completed successfully."
        }
    }

    fun blockSuspiciousIp(ip: String = "198.51.100.24") {
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordSecurityEvent(
                SecurityEvent(
                    title = "Firewall Ingress Block: $ip",
                    eventType = "INTRUSION_ALERT",
                    severity = "HIGH",
                    details = "IP $ip blacklisted permanently across all city gateways.",
                    timestampStr = "Just now"
                )
            )
            _activeThreatCount.value = (_activeThreatCount.value - 1).coerceAtLeast(0)
            repository.logAudit(
                user = _currentRole.value.displayName,
                role = _currentRole.value.name,
                action = "Blocked Ingress IP",
                targetResource = "Firewall Rule -> $ip",
                result = "SUCCESS"
            )
            _globalBannerMessage.value = "IP $ip permanently blocked by Firewall."
        }
    }

    // Demo Simulation Flow (City Data -> AI detects anomaly -> Alert -> Map -> Resource / Security response)
    fun simulateLiveCityAnomaly() {
        viewModelScope.launch(Dispatchers.IO) {
            val newIncident = CivicIncident(
                title = "Flash Gridlock & Heavy Smog Influx",
                type = "AIR_QUALITY",
                locationName = "Downtown Commerce Tunnel",
                latitudeRatio = 0.50f,
                longitudeRatio = 0.50f,
                severity = "CRITICAL",
                status = "ACTIVE",
                detectedAt = "Just now",
                aiAnalysis = "CO2 levels +58% above baseline within 12 minutes. Traffic bottleneck detected at exit 3.",
                confidence = 94,
                relatedEvents = "Tunnel ventilation fan #2 reduced capacity"
            )
            repository.addIncident(newIncident)

            repository.addAlert(
                AlertItem(
                    title = "Critical Air Anomaly: Downtown Tunnel",
                    message = "Unusual CO2 surge (+58%). Ventilation failure correlated with rush hour jam.",
                    category = "AI",
                    severity = "CRITICAL",
                    timeAgo = "Just now",
                    targetScreen = "INCIDENTS"
                )
            )

            repository.logAudit(
                user = "CityPulse AI Sentinel",
                role = "SYSTEM_AI",
                action = "Detected Anomaly via Multi-Sensor Correlation",
                targetResource = "Downtown Commerce Tunnel",
                result = "FLAGGED"
            )

            _globalBannerMessage.value = "🚨 AI Anomaly Triggered: Downtown Tunnel CO2 & Traffic Alert!"
        }
    }

    fun acknowledgeAlert(alertId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.acknowledgeAlert(alertId)
        }
    }
}
