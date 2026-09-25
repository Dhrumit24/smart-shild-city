package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartShieldDao {

    // Incidents
    @Query("SELECT * FROM civic_incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<CivicIncident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: CivicIncident): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidents(incidents: List<CivicIncident>)

    @Update
    suspend fun updateIncident(incident: CivicIncident)

    @Query("UPDATE civic_incidents SET status = :status, assignedTeam = :team WHERE id = :id")
    suspend fun updateIncidentStatus(id: Int, status: String, team: String?)

    // Resources
    @Query("SELECT * FROM resource_offers ORDER BY timestamp DESC")
    fun getAllResources(): Flow<List<ResourceOffer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: ResourceOffer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<ResourceOffer>)

    @Query("UPDATE resource_offers SET status = :status WHERE id = :id")
    suspend fun updateResourceStatus(id: Int, status: String)

    // Shelters
    @Query("SELECT * FROM shelters")
    fun getAllShelters(): Flow<List<ShelterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelters(shelters: List<ShelterEntity>)

    // Dispatch Tasks
    @Query("SELECT * FROM dispatch_tasks ORDER BY timestamp DESC")
    fun getAllDispatchTasks(): Flow<List<DispatchTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispatchTask(task: DispatchTask): Long

    @Query("UPDATE dispatch_tasks SET status = :status WHERE id = :id")
    suspend fun updateDispatchStatus(id: Int, status: String)

    // API Targets
    @Query("SELECT * FROM api_targets")
    fun getAllApiTargets(): Flow<List<ApiTarget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApiTarget(target: ApiTarget): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApiTargets(targets: List<ApiTarget>)

    @Update
    suspend fun updateApiTarget(target: ApiTarget)

    // Vulnerabilities
    @Query("SELECT * FROM vulnerability_findings ORDER BY id DESC")
    fun getAllVulnerabilities(): Flow<List<VulnerabilityFinding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVulnerability(vulnerability: VulnerabilityFinding): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVulnerabilities(vulnerabilities: List<VulnerabilityFinding>)

    @Query("UPDATE vulnerability_findings SET status = :status WHERE id = :id")
    suspend fun updateVulnerabilityStatus(id: Int, status: String)

    // Security Events
    @Query("SELECT * FROM security_events ORDER BY timestamp DESC LIMIT 50")
    fun getAllSecurityEvents(): Flow<List<SecurityEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityEvent(event: SecurityEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityEvents(events: List<SecurityEvent>)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AuditLog>)

    // Alerts
    @Query("SELECT * FROM system_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertItem>)

    @Query("UPDATE system_alerts SET isAcknowledged = 1 WHERE id = :id")
    suspend fun acknowledgeAlert(id: Int)
}
