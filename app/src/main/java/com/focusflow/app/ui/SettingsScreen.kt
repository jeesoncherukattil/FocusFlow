package com.focusflow.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var biometric by remember { mutableStateOf(false) }
    var iot by remember { mutableStateOf(false) }
    var reelCounter by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("Customize how FocusFlow works", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(24.dp))

        SettingCard("Biometric Adaptation", "Adjust focus length using heart rate from Health Connect", biometric) { biometric = it }
        Spacer(Modifier.height(12.dp))
        SettingCard("Smart Home", "Trigger lights or plugs when a focus session starts", iot) { iot = it }
        Spacer(Modifier.height(12.dp))
        SettingCard("Reel Counter", "Show a floating counter during breaks", reelCounter) { reelCounter = it }

        Spacer(Modifier.height(32.dp))
        Text("More integrations coming soon", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}

@Composable
private fun SettingCard(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}
