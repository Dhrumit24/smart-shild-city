package com.example.data

import kotlinx.coroutines.flow.Flow

class SmartShieldRepository(private val dao: SmartShieldDao) {

    // Incidents
    val allIncidents: Flow<List<CivicIncident>> = dao.getAllIncidents()

    suspend fun addIncident(incident: CivicIncident): Long = dao.insertIncident(incident)

    suspend fun updateIncidentStatus(id: Int, status: String, team: String?) =
        dao.updateIncidentStatus(id, status, team)

    // Resources
    val allResources: Flow<List<ResourceOffer>> = dao.getAllResources()

    suspend fun addResource(resource: ResourceOffer): Long = dao.insertResource(resource)

    suspend fun updateResourceStatus(id: Int, status: String) =
        dao.updateResourceStatus(id, status)

    // Shelters
    val allShelters: Flow<List<ShelterEntity>> = dao.getAllShelters()

    // Dispatch Tasks
    val allDispatchTasks: Flow<List<DispatchTask>> = dao.getAllDispatchTasks()

    suspend fun createDispatchTask(task: DispatchTask): Long = dao.insertDispatchTask(task)

    suspend fun updateDispatchStatus(id: Int, status: String) = dao.updateDispatchStatus(id, status)

    // API Targets & Vulnerabilities
    val allApiTargets: Flow<List<ApiTarget>> = dao.getAllApiTargets()

    val allVulnerabilities: Flow<List<VulnerabilityFinding>> = dao.getAllVulnerabilities()

    suspend fun addApiTarget(target: ApiTarget): Long = dao.insertApiTarget(target)

    suspend fun updateApiTarget(target: ApiTarget) = dao.updateApiTarget(target)

    suspend fun addVulnerability(vulnerability: VulnerabilityFinding): Long =
        dao.insertVulnerability(vulnerability)

    suspend fun resolveVulnerability(id: Int) =
        dao.updateVulnerabilityStatus(id, "RESOLVED")

    // Security Events & Threat
    val allSecurityEvents: Flow<List<SecurityEvent>> = dao.getAllSecurityEvents()

    suspend fun recordSecurityEvent(event: SecurityEvent): Long = dao.insertSecurityEvent(event)

    // Audit Logs
    val allAuditLogs: Flow<List<AuditLog>> = dao.getAllAuditLogs()

    suspend fun logAudit(
        user: String,
        role: String,
        action: String,
        targetResource: String,
        result: String,
        ipSession: String = "10.0.4.18 [SECURE-VPN]"
    ) {
        dao.insertAuditLog(
            AuditLog(
                user = user,
                role = role,
                action = action,
                targetResource = targetResource,
                result = result,
                ipSession = ipSession,
                timestampStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
        )
    }

    // Alerts
    val allAlerts: Flow<List<AlertItem>> = dao.getAllAlerts()

    suspend fun addAlert(alert: AlertItem): Long = dao.insertAlert(alert)

    suspend fun acknowledgeAlert(id: Int) = dao.acknowledgeAlert(id)
}
