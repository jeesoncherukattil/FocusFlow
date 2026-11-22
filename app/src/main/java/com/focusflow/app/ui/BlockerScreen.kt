package com.focusflow.app.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.focusflow.app.service.AppBlocker
import com.focusflow.app.service.BlockedAppsStore

data class DistractionApp(val name: String, val packageName: String, val icon: String)

@Composable
fun BlockerScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val apps = listOf(
        DistractionApp("Instagram", "com.instagram.android", "IG"),
        DistractionApp("YouTube", "com.google.android.youtube", "YT"),
        DistractionApp("TikTok", "com.zhiliaoapp.musically", "TT"),
        DistractionApp("X", "com.twitter.android", "X"),
        DistractionApp("Facebook", "com.facebook.katana", "FB"),
        DistractionApp("Chrome", "com.android.chrome", "CH")
    )

    val blocked by BlockedAppsStore.packages.collectAsState()
    val blocker = remember { AppBlocker(context) }
    var hasPermission by remember { mutableStateOf(blocker.hasUsagePermission()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = blocker.hasUsagePermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text("Block Distractions", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("These apps are blocked while you focus", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(20.dp))

        if (!hasPermission) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Permission needed", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Text("Grant Usage Access so FocusFlow can detect when you open blocked apps.", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }, shape = RoundedCornerShape(12.dp)) {
                        Text("Open Settings")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        } else {
            Text("\u2713 Usage Access granted", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(apps) { app ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(app.icon, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(app.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                        Switch(
                            checked = blocked.contains(app.packageName),
                            onCheckedChange = { BlockedAppsStore.toggle(context.applicationContext as android.app.Application, app.packageName) }
                        )
                    }
                }
            }
        }
    }
}
