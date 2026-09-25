package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CivicIncident::class,
        ResourceOffer::class,
        ShelterEntity::class,
        DispatchTask::class,
        ApiTarget::class,
        VulnerabilityFinding::class,
        SecurityEvent::class,
        AuditLog::class,
        AlertItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartShieldDatabase : RoomDatabase() {
    abstract fun smartShieldDao(): SmartShieldDao

    companion object {
        @Volatile
        private var INSTANCE: SmartShieldDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SmartShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartShieldDatabase::class.java,
                    "smartshield_city_db"
                ).addCallback(DatabaseSeedCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseSeedCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.smartShieldDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: SmartShieldDao) {
            // Seed Civic Incidents
            dao.insertIncidents(
                listOf(
                    CivicIncident(
                        title = "Severe Traffic Congestion Spike",
                        type = "TRAFFIC",
                        locationName = "MG Road & Central Hub",
                        latitudeRatio = 0.35f,
                        longitudeRatio = 0.42f,
                        severity = "HIGH",
                        status = "ACTIVE",
                        detectedAt = "10:42 AM",
                        aiAnalysis = "Traffic density is 42% above baseline. Correlated with heavy rainfall in Sector 4 and delayed bus line 12.",
                        confidence = 88,
                        assignedTeam = "Traffic Control Team Alpha"
                    ),
                    CivicIncident(
                        title = "PM2.5 Air Quality Spike (AQI 218)",
                        type = "AIR_QUALITY",
                        locationName = "Zone C Industrial Park",
                        latitudeRatio = 0.65f,
                        longitudeRatio = 0.72f,
                        severity = "CRITICAL",
                        status = "ACTIVE",
                        detectedAt = "10:15 AM",
                        aiAnalysis = "Particulate matter surged 65% in 30 mins. Wind direction 12mph North-East pushing towards residential sector.",
                        confidence = 94,
                        assignedTeam = "EcoResponse Unit 3"
                    ),
                    CivicIncident(
                        title = "Heavy Rain & Flash Flood Risk",
                        type = "WEATHER",
                        locationName = "South River Valley Corridor",
                        latitudeRatio = 0.78f,
                        longitudeRatio = 0.28f,
                        severity = "HIGH",
                        status = "ACTIVE",
                        detectedAt = "09:50 AM",
                        aiAnalysis = "Sensor water depth +14cm/hr. Automatic sluice gates recommended for activation.",
                        confidence = 91,
                        assignedTeam = null
                    ),
                    CivicIncident(
                        title = "Metro Rail Line 3 Signal Anomaly",
                        type = "TRANSIT",
                        locationName = "North Metro Terminal",
                        latitudeRatio = 0.22f,
                        longitudeRatio = 0.68f,
                        severity = "MEDIUM",
                        status = "RESPONDING",
                        detectedAt = "10:30 AM",
                        aiAnalysis = "Automatic signaling delay of 8.5 minutes across 4 stations. Commuter load rerouted to Bus Rapid Transit.",
                        confidence = 82,
                        assignedTeam = "Transit Technical Crew"
                    ),
                    CivicIncident(
                        title = "Substation Transformer Overheat",
                        type = "POWER",
                        locationName = "East Grid Substation 9",
                        latitudeRatio = 0.45f,
                        longitudeRatio = 0.85f,
                        severity = "HIGH",
                        status = "RESPONDING",
                        detectedAt = "10:02 AM",
                        aiAnalysis = "Thermal scan detected 94°C on auxiliary transformer. Redundant loop activated to prevent blackout.",
                        confidence = 96,
                        assignedTeam = "Municipal Power Grid Ops"
                    ),
                    CivicIncident(
                        title = "Commercial Kitchen Surplus Reported",
                        type = "RESOURCE",
                        locationName = "Grand Hyatt Culinary Wing",
                        latitudeRatio = 0.40f,
                        longitudeRatio = 0.35f,
                        severity = "LOW",
                        status = "ACTIVE",
                        detectedAt = "10:38 AM",
                        aiAnalysis = "High volume surplus (75 warm meals). 94% optimal match to Hope Haven Shelter (3.2 km).",
                        confidence = 95,
                        assignedTeam = "Driver Rahul Sharma"
                    ),
                    CivicIncident(
                        title = "API Auth Failure Anomaly Detected",
                        type = "SECURITY",
                        locationName = "Municipal SCADA Control Node",
                        latitudeRatio = 0.52f,
                        longitudeRatio = 0.55f,
                        severity = "CRITICAL",
                        status = "ACTIVE",
                        detectedAt = "10:44 AM",
                        aiAnalysis = "Brute force pattern against SCADA Gateway: 34 failed auth attempts in 60 seconds from unauthorized subnet.",
                        confidence = 99,
                        assignedTeam = "Cyber Defense Taskforce"
                    )
                )
            )

            // Seed Resource Offers (Surplus-to-Shelter)
            dao.insertResources(
                listOf(
                    ResourceOffer(
                        donorName = "Grand Hyatt Banquet Kitchen",
                        foodType = "Prepared Meals",
                        quantity = "75 Warm Meals",
                        pickupLocation = "Bay 4, Commercial Ring Rd",
                        expiresAt = "1h 45m",
                        status = "MATCHED",
                        bestMatchShelter = "Hope Haven Community Shelter",
                        matchScore = 96,
                        distanceKm = 2.8f
                    ),
                    ResourceOffer(
                        donorName = "City Center Organic Supermarket",
                        foodType = "Fresh Produce & Dairy",
                        quantity = "140 kg Fresh Greens & Milk",
                        pickupLocation = "Dock B, Downtown Plaza",
                        expiresAt = "4h 00m",
                        status = "AVAILABLE",
                        bestMatchShelter = "Downtown Respite Care",
                        matchScore = 91,
                        distanceKm = 3.5f
                    ),
                    ResourceOffer(
                        donorName = "Artisan Bakery & Co.",
                        foodType = "Bakery & Breads",
                        quantity = "60 Fresh Loaves & Pastries",
                        pickupLocation = "Old Town Bakery Lane",
                        expiresAt = "6h 30m",
                        status = "AVAILABLE",
                        bestMatchShelter = "Sunrise Youth Center",
                        matchScore = 88,
                        distanceKm = 1.9f
                    ),
                    ResourceOffer(
                        donorName = "TechPark Corporate Cafeteria",
                        foodType = "Packaged Meals",
                        quantity = "50 Sandwiches & Juice",
                        pickupLocation = "TechPark Tower C",
                        expiresAt = "2h 15m",
                        status = "DISPATCHED",
                        bestMatchShelter = "St. Jude Emergency Shelter",
                        matchScore = 94,
                        distanceKm = 4.1f
                    )
                )
            )

            // Seed Shelters
            dao.insertShelters(
                listOf(
                    ShelterEntity(
                        name = "Hope Haven Community Shelter",
                        address = "42 Civic Care Blvd, Sector 3",
                        capacity = 100,
                        currentOccupancy = 78,
                        needLevel = "CRITICAL",
                        acceptedFoodTypes = "Prepared Meals, Fresh Produce",
                        distanceKm = 2.8f
                    ),
                    ShelterEntity(
                        name = "Downtown Respite Care",
                        address = "118 Pine St, Central District",
                        capacity = 80,
                        currentOccupancy = 45,
                        needLevel = "HIGH",
                        acceptedFoodTypes = "All Categories",
                        distanceKm = 3.5f
                    ),
                    ShelterEntity(
                        name = "Sunrise Youth Sanctuary",
                        address = "27 East River Way",
                        capacity = 50,
                        currentOccupancy = 25,
                        needLevel = "MODERATE",
                        acceptedFoodTypes = "Bakery, Packaged Goods",
                        distanceKm = 1.9f
                    ),
                    ShelterEntity(
                        name = "St. Jude Emergency Shelter",
                        address = "89 South Bridge Rd",
                        capacity = 120,
                        currentOccupancy = 95,
                        needLevel = "HIGH",
                        acceptedFoodTypes = "Prepared Meals, Dairy",
                        distanceKm = 4.1f
                    )
                )
            )

            // Seed Dispatch Tasks
            dao.insertDispatchTask(
                DispatchTask(
                    taskId = "DISP-#1024",
                    donorName = "Grand Hyatt Banquet Kitchen",
                    recipientName = "Hope Haven Community Shelter",
                    driverName = "Rahul Sharma",
                    distanceKm = 2.8f,
                    etaMinutes = 9,
                    status = "IN_TRANSIT",
                    routeNotes = "Route optimized: Bypass MG Road due to traffic congestion. ETA 9 min."
                )
            )
            dao.insertDispatchTask(
                DispatchTask(
                    taskId = "DISP-#1025",
                    donorName = "TechPark Corporate Cafeteria",
                    recipientName = "St. Jude Emergency Shelter",
                    driverName = "Meera Sen",
                    distanceKm = 4.1f,
                    etaMinutes = 14,
                    status = "PICKUP_STARTED",
                    routeNotes = "Driver arrived at TechPark Tower C loading dock."
                )
            )

            // Seed API Targets (SentinelAPI)
            dao.insertApiTargets(
                listOf(
                    ApiTarget(
                        name = "Municipal Transit & Fleet Gateway",
                        baseUrl = "https://api.citypulse.gov/v2/fleet",
                        status = "VULNERABLE",
                        endpointsCount = 48,
                        criticalCount = 2,
                        highCount = 4,
                        mediumCount = 6,
                        lastScanned = "Today, 10:35 AM",
                        securityScore = 74
                    ),
                    ApiTarget(
                        name = "Civic Water SCADA Monitoring",
                        baseUrl = "https://scada.water.smartshield.gov",
                        status = "SECURE",
                        endpointsCount = 32,
                        criticalCount = 0,
                        highCount = 1,
                        mediumCount = 2,
                        lastScanned = "Today, 09:12 AM",
                        securityScore = 93
                    ),
                    ApiTarget(
                        name = "Public Citizen Grievance Portal",
                        baseUrl = "https://portal.citizen.gov/api/v1",
                        status = "VULNERABLE",
                        endpointsCount = 64,
                        criticalCount = 1,
                        highCount = 3,
                        mediumCount = 8,
                        lastScanned = "Yesterday, 04:20 PM",
                        securityScore = 81
                    )
                )
            )

            // Seed Vulnerabilities
            dao.insertVulnerabilities(
                listOf(
                    VulnerabilityFinding(
                        apiTargetId = 1,
                        apiName = "Municipal Transit & Fleet Gateway",
                        endpoint = "/api/v2/fleet/vehicles/{id}/telemetry",
                        httpMethod = "GET",
                        vulnerabilityType = "Broken Object Level Authorization (BOLA)",
                        severity = "CRITICAL",
                        description = "API returns telemetry of any fleet vehicle without verifying if caller belongs to assigned department.",
                        evidence = "GET /api/v2/fleet/vehicles/9021/telemetry with operator-token returned vehicle GPS, fuel & driver PII.",
                        impact = "Unauthorized actors can track location of municipal emergency response vehicles and public transport.",
                        recommendation = "Implement claims-based policy checks verifying vehicle tenantId against JWT claims.",
                        status = "OPEN",
                        detectedAt = "10:35 AM"
                    ),
                    VulnerabilityFinding(
                        apiTargetId = 1,
                        apiName = "Municipal Transit & Fleet Gateway",
                        endpoint = "/api/v2/auth/token/refresh",
                        httpMethod = "POST",
                        vulnerabilityType = "Missing Rate Limiting & Brute Force",
                        severity = "HIGH",
                        description = "Endpoint does not apply token bucket or sliding window rate limiting on refresh tokens.",
                        evidence = "Sent 120 consecutive requests within 3 seconds with HTTP 200 OK responses.",
                        impact = "Enables credential stuffing, token guessing, and denial-of-service against auth infrastructure.",
                        recommendation = "Enforce rate limit of 10 requests/minute per IP and require re-authentication on anomaly.",
                        status = "OPEN",
                        detectedAt = "10:36 AM"
                    ),
                    VulnerabilityFinding(
                        apiTargetId = 3,
                        apiName = "Public Citizen Grievance Portal",
                        endpoint = "/api/v1/complaints/{id}",
                        httpMethod = "GET",
                        vulnerabilityType = "Excessive Data Exposure",
                        severity = "HIGH",
                        description = "API response includes citizen National ID hash, phone number, and physical home coordinates.",
                        evidence = "JSON response contains unfiltered internal database user entity.",
                        impact = "Violates data privacy regulations and risks dox/harassment of reporting citizens.",
                        recommendation = "Use data transfer objects (DTOs) with strict field projection to mask private attributes.",
                        status = "OPEN",
                        detectedAt = "Yesterday"
                    )
                )
            )

            // Seed Security Events (Deadman's Drive)
            dao.insertSecurityEvents(
                listOf(
                    SecurityEvent(
                        title = "Brute Force Pattern Detected",
                        eventType = "INTRUSION_ALERT",
                        severity = "CRITICAL",
                        details = "34 invalid authentication attempts intercepted from IP 198.51.100.24 targeting /api/v2/auth.",
                        timestampStr = "10:44 AM"
                    ),
                    SecurityEvent(
                        title = "Hardware Security Key Verified",
                        eventType = "USB_VERIFY",
                        severity = "INFO",
                        details = "Cryptographic FIDO2 hardware token verified for Operator Terminal #1.",
                        timestampStr = "10:35 AM"
                    ),
                    SecurityEvent(
                        title = "Failed Root Login",
                        eventType = "AUTH_FAILURE",
                        severity = "HIGH",
                        details = "Failed administrative login from workstation WS-04. Threshold 2/5 remaining.",
                        timestampStr = "10:28 AM"
                    ),
                    SecurityEvent(
                        title = "Encrypted Cold Snapshot Generated",
                        eventType = "BACKUP_TRIGGER",
                        severity = "INFO",
                        details = "City operational state snapshot encrypted with AES-GCM-256 and backed up to vault.",
                        timestampStr = "09:00 AM"
                    )
                )
            )

            // Seed Audit Logs
            dao.insertAuditLogs(
                listOf(
                    AuditLog(
                        user = "Admin (Chief SecOps)",
                        role = "ADMIN",
                        action = "Triggered API Security Scan",
                        targetResource = "Municipal Transit & Fleet Gateway",
                        result = "SUCCESS",
                        ipSession = "10.0.4.18 [SECURE-VPN]",
                        timestampStr = "10:35 AM"
                    ),
                    AuditLog(
                        user = "Meera Sen",
                        role = "NGO_COORDINATOR",
                        action = "Dispatched Surplus Rescue Route",
                        targetResource = "Grand Hyatt -> Hope Haven",
                        result = "SUCCESS",
                        ipSession = "10.0.7.52 [CIVIC-WIFI]",
                        timestampStr = "10:39 AM"
                    ),
                    AuditLog(
                        user = "Automated Shield Sentinel",
                        role = "SYSTEM_AI",
                        action = "IP Rate-Limit Isolation Rule Applied",
                        targetResource = "Firewall Rule #994 (198.51.100.24)",
                        result = "FLAGGED",
                        ipSession = "127.0.0.1 [KERNEL]",
                        timestampStr = "10:44 AM"
                    ),
                    AuditLog(
                        user = "City Operator Alpha",
                        role = "CITY_STAFF",
                        action = "Rerouted Bus Fleet Line 12",
                        targetResource = "Transit Control Grid",
                        result = "SUCCESS",
                        ipSession = "10.0.2.10 [COMMAND-STATION]",
                        timestampStr = "10:43 AM"
                    )
                )
            )

            // Seed System Alerts
            dao.insertAlerts(
                listOf(
                    AlertItem(
                        title = "API Authorization Vulnerability Detected",
                        message = "BOLA vulnerability exposed on /api/v2/fleet/vehicles/{id}/telemetry.",
                        category = "SECURITY",
                        severity = "CRITICAL",
                        timeAgo = "2 min ago",
                        targetScreen = "SECURITY"
                    ),
                    AlertItem(
                        title = "Zone A Traffic Anomaly Spike",
                        message = "Unusual congestion (+42% baseline) on MG Road near Central Hub.",
                        category = "CIVIC",
                        severity = "HIGH",
                        timeAgo = "8 min ago",
                        targetScreen = "INCIDENTS"
                    ),
                    AlertItem(
                        title = "Surplus Food Expiration Warning",
                        message = "75 warm meals at Grand Hyatt will expire in 1h 45m. Fast match ready.",
                        category = "RESOURCE",
                        severity = "MEDIUM",
                        timeAgo = "15 min ago",
                        targetScreen = "RESOURCES"
                    ),
                    AlertItem(
                        title = "Weather Warning: Sluice Gate Protocol",
                        message = "South River Valley water sensor exceeds +14cm/hr warning threshold.",
                        category = "CIVIC",
                        severity = "HIGH",
                        timeAgo = "45 min ago",
                        targetScreen = "INCIDENTS"
                    )
                )
            )
        }
    }
}
