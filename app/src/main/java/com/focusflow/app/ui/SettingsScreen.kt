package com.focusflow.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var biometric by remember { mutableStateOf(false) }
    var iot by remember { mutableStateOf(false) }
    var reelCounter by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        SettingRow("Biometric Adaptive Scheduling", "Adjust intervals from Health Connect / Wear OS", biometric) { biometric = it }
        SettingRow("Smart Home Sync", "Trigger lights / plugs via Tuya on focus start", iot) { iot = it }
        SettingRow("Reel Counter Overlay", "Show live Reels watched during breaks", reelCounter) { reelCounter = it }
        Spacer(Modifier.height(24.dp))
        Text("Environment", style = MaterialTheme.typography.titleMedium)
        Text("Link Tuya / Wipro devices in a future update.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}
