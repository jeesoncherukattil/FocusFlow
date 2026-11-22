package com.focusflow.app.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.focusflow.app.service.AppBlocker
import com.focusflow.app.service.BlockedAppsStore

data class DistractionApp(val name: String, val packageName: String)

@Composable
fun BlockerScreen() {
    val context = LocalContext.current
    val apps = listOf(
        DistractionApp("Instagram", "com.instagram.android"),
        DistractionApp("YouTube", "com.google.android.youtube"),
        DistractionApp("TikTok", "com.zhiliaoapp.musically"),
        DistractionApp("X / Twitter", "com.twitter.android"),
        DistractionApp("Facebook", "com.facebook.katana"),
        DistractionApp("Chrome", "com.android.chrome")
    )

    val blocked by BlockedAppsStore.packages.collectAsState()
    var strictMode by remember { mutableStateOf(false) }
    val blocker = remember { AppBlocker(context) }
    var hasPermission by remember { mutableStateOf(blocker.hasUsagePermission()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("App Blocker", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Apps blocked during focus sessions", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))

        if (!hasPermission) {
            Button(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant Usage Access Permission")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Required for app blocking. After granting, return here.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(16.dp))
        } else {
            Text("Usage Access granted", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Strict Mode", style = MaterialTheme.typography.titleMedium)
                Text("Prevent bypassing the block", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = strictMode, onCheckedChange = { strictMode = it })
        }

        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(apps) { app ->
                Card(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(app.name, modifier = Modifier.weight(1f))
                        Switch(
                            checked = blocked.contains(app.packageName),
                            onCheckedChange = { BlockedAppsStore.toggle(app.packageName) }
                        )
                    }
                }
            }
        }
    }
}
