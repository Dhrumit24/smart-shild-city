package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "civic_incidents")
data class CivicIncident(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // TRAFFIC, WEATHER, AIR_QUALITY, TRANSIT, POWER, SECURITY
    val locationName: String,
    val latitudeRatio: Float, // 0.0f - 1.0f on city map grid
    val longitudeRatio: Float, // 0.0f - 1.0f on city map grid
    val severity: String, // CRITICAL, HIGH, MEDIUM, LOW
    val status: String, // ACTIVE, RESPONDING, RESOLVED
    val detectedAt: String,
    val aiAnalysis: String,
    val confidence: Int,
    val assignedTeam: String? = null,
    val relatedEvents: String = "Weather alert nearby",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "resource_offers")
data class ResourceOffer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val donorName: String,
    val foodType: String,
    val quantity: String,
    val pickupLocation: String,
    val expiresAt: String,
    val status: String, // AVAILABLE, MATCHED, DISPATCHED, DELIVERED
    val bestMatchShelter: String,
    val matchScore: Int,
    val distanceKm: Float,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "shelters")
data class ShelterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val capacity: Int,
    val currentOccupancy: Int,
    val needLevel: String, // CRITICAL, HIGH, MODERATE
    val acceptedFoodTypes: String,
    val distanceKm: Float
)

@Entity(tableName = "dispatch_tasks")
data class DispatchTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: String,
    val donorName: String,
    val recipientName: String,
    val driverName: String,
    val distanceKm: Float,
    val etaMinutes: Int,
    val status: String, // ASSIGNED, PICKUP_STARTED, IN_TRANSIT, DELIVERED
    val routeNotes: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "api_targets")
data class ApiTarget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val baseUrl: String,
    val status: String, // SCANNED, SCANNING, SECURE, VULNERABLE
    val endpointsCount: Int,
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val lastScanned: String,
    val securityScore: Int
)

@Entity(tableName = "vulnerability_findings")
data class VulnerabilityFinding(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val apiTargetId: Int,
    val apiName: String,
    val endpoint: String,
    val httpMethod: String,
    val vulnerabilityType: String,
    val severity: String, // CRITICAL, HIGH, MEDIUM
    val description: String,
    val evidence: String,
    val impact: String,
    val recommendation: String,
    val status: String = "OPEN", // OPEN, RESOLVED
    val detectedAt: String = "Just now"
)

@Entity(tableName = "security_events")
data class SecurityEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val eventType: String, // AUTH_FAILURE, USB_VERIFY, INTRUSION_ALERT, BACKUP_TRIGGER, SESSION_LOCK
    val severity: String, // CRITICAL, HIGH, INFO
    val details: String,
    val timestampStr: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val user: String,
    val role: String,
    val action: String,
    val targetResource: String,
    val result: String, // SUCCESS, DENIED, FLAGGED
    val ipSession: String,
    val timestampStr: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "system_alerts")
data class AlertItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val category: String, // CIVIC, SECURITY, RESOURCE, AI
    val severity: String, // CRITICAL, HIGH, MEDIUM, RESOLVED
    val timeAgo: String,
    val isAcknowledged: Boolean = false,
    val targetScreen: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
