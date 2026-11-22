package com.focusflow.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusflow.app.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = viewModel()) {
    val sessions by viewModel.sessions.collectAsState(initial = emptyList())
    val todayMinutes by viewModel.todayFocusMinutes.collectAsState(initial = 0)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Analytics", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        StatCard("Focus minutes today", "${todayMinutes ?: 0} min")
        Spacer(Modifier.height(12.dp))
        StatCard("Total sessions", "${sessions.size}")
        Spacer(Modifier.height(12.dp))
        StatCard("Completed", "${sessions.count { it.completed }}")
        Spacer(Modifier.height(24.dp))
        Text("Recent sessions", style = MaterialTheme.typography.titleMedium)
        sessions.take(5).forEach { s ->
            Text("${s.activityTag} · ${s.focusMinutes} min · ${if (s.completed) "Done" else "Partial"}")
        }
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
