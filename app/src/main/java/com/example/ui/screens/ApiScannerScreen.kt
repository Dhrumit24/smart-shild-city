package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ScanProgressState
import com.example.ui.ScreenNav
import com.example.ui.theme.*

@Composable
fun ApiScannerScreen(
    scanProgress: ScanProgressState,
    onStartScan: (String, String) -> Unit,
    onNavigate: (ScreenNav) -> Unit,
    modifier: Modifier = Modifier
) {
    var apiName by remember { mutableStateOf("City Fleet & Telematics Gateway") }
    var swaggerUrl by remember { mutableStateOf("https://api.citypulse.gov/v2/openapi.json") }
    var authorizedConsent by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommandBackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Radar, contentDescription = null, tint = CyanAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SentinelAPI Scanner",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                }
                Text(
                    text = "Automated OWASP API Top 10 vulnerability inspection",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }
        }

        // Scanner Configuration Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Scan Target Specification",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )

                    OutlinedTextField(
                        value = apiName,
                        onValueChange = { apiName = it },
                        label = { Text("API Gateway Name", color = TextSecondaryDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark,
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CommandBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = swaggerUrl,
                        onValueChange = { swaggerUrl = it },
                        label = { Text("OpenAPI / Swagger 3.0 URL", color = TextSecondaryDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark,
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CommandBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Authorization Consent Checkbox (Critical guardrail)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = authorizedConsent,
                            onCheckedChange = { authorizedConsent = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CyanAccent,
                                uncheckedColor = CommandBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I confirm I am explicitly authorized to perform security testing on this target sandbox API.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondaryDark
                        )
                    }

                    Button(
                        onClick = { onStartScan(apiName, swaggerUrl) },
                        enabled = authorizedConsent && !scanProgress.isScanning,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("execute_scan_button")
                    ) {
                        if (scanProgress.isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Auditing Endpoints...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Automated Security Scan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Scanning Progress Status
        if (scanProgress.isScanning || scanProgress.progress > 0f) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (scanProgress.isScanning) CyanAccent else StatusSafe),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (scanProgress.isScanning) "Scan in Progress..." else "Security Audit Complete ✓",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (scanProgress.isScanning) CyanAccent else StatusSafe
                            )
                            Text(
                                text = "${(scanProgress.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { scanProgress.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CyanAccent,
                            trackColor = CommandSurfaceHighlight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = scanProgress.currentStep,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryDark
                        )

                        Text(
                            text = "Endpoints scanned: ${scanProgress.scannedEndpoints} / ${scanProgress.totalEndpoints}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )

                        scanProgress.completedSummary?.let { summary ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = CommandSurfaceDark,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Scan Summary:",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimaryDark
                                    )
                                    Text(
                                        text = summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AlertCritical
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onNavigate(ScreenNav.SECURITY) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AlertCritical, contentColor = Color.White),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Inspect Discovered Findings", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // OWASP API Security Checklist
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CommandCardDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CommandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Automated Checks Performed",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val checks = listOf(
                        "API1:2023 - Broken Object Level Authorization (BOLA)" to true,
                        "API2:2023 - Broken Authentication & Token Validation" to true,
                        "API3:2023 - Broken Object Property Level Auth" to true,
                        "API4:2023 - Unrestricted Resource Consumption (Rate Limiting)" to true,
                        "API5:2023 - Broken Function Level Authorization (BFLA)" to true
                    )

                    checks.forEach { (name, enabled) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSafe, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(name, fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }
        }
    }
}
