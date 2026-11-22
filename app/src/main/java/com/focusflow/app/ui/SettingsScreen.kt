package com.focusflow.app.ui

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focusflow.app.cloud.CloudSyncManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var biometric by remember { mutableStateOf(false) }
    var iot by remember { mutableStateOf(false) }
    var reelCounter by remember { mutableStateOf(true) }
    var googleEmail by remember { mutableStateOf(CloudSyncManager.getGoogleEmail(context)) }
    var msSignedIn by remember { mutableStateOf(CloudSyncManager.isMicrosoftSignedIn(context)) }
    var status by remember { mutableStateOf("") }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            googleEmail = CloudSyncManager.getGoogleEmail(context)
            status = "Signed in with Google"
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("Customize how FocusFlow works", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(24.dp))

        SettingCard("Biometric Adaptation", "Adjust focus length using heart rate from Health Connect", biometric) { biometric = it }
        Spacer(Modifier.height(12.dp))
        SettingCard("Smart Home", "Trigger lights or plugs when a focus session starts", iot) { iot = it }
        Spacer(Modifier.height(12.dp))
        SettingCard("Reel Counter", "Show a floating counter during breaks", reelCounter) { reelCounter = it }

        Spacer(Modifier.height(28.dp))
        Text("Cloud backup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text("Save your sessions and blocked apps", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Google Drive", fontWeight = FontWeight.Medium)
                Text(googleEmail ?: "Not signed in", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (googleEmail == null) {
                        Button(onClick = { googleLauncher.launch(CloudSyncManager.getGoogleSignInIntent(context)) }) {
                            Text("Sign in with Google")
                        }
                    } else {
                        Button(onClick = {
                            scope.launch {
                                CloudSyncManager.uploadToGoogleDrive(context, "{\"sessions\":[],\"blocked\":[]}")
                                status = "Backed up to Google Drive"
                            }
                        }) { Text("Backup now") }
                        OutlinedButton(onClick = {
                            com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                            googleEmail = null
                        }) { Text("Sign out") }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Microsoft OneDrive", fontWeight = FontWeight.Medium)
                Text(if (msSignedIn) "Signed in" else "Not signed in", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!msSignedIn) {
                        Button(onClick = {
                            context.startActivity(CloudSyncManager.getMicrosoftSignInIntent())
                            CloudSyncManager.setMicrosoftSignedIn(context, true)
                            msSignedIn = true
                            status = "Microsoft sign-in started"
                        }) { Text("Sign in with Microsoft") }
                    } else {
                        Button(onClick = {
                            scope.launch {
                                CloudSyncManager.uploadToOneDrive(context, "{\"sessions\":[],\"blocked\":[]}")
                                status = "Backed up to OneDrive"
                            }
                        }) { Text("Backup now") }
                        OutlinedButton(onClick = {
                            CloudSyncManager.setMicrosoftSignedIn(context, false)
                            msSignedIn = false
                        }) { Text("Sign out") }
                    }
                }
            }
        }

        if (status.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(status, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SettingCard(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}
