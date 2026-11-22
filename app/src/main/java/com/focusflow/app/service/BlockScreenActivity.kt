package com.focusflow.app.service

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.focusflow.app.ui.theme.FocusFlowTheme

class BlockScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusFlowTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Focus Mode Active", style = MaterialTheme.typography.headlineMedium)
                    Text("Return to your study session", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = { finish() }) { Text("OK") }
                }
            }
        }
    }
}
