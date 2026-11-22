package com.focusflow.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusflow.app.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = viewModel()) {
    val sessions by viewModel.sessions.collectAsState(initial = emptyList())
    val todayMinutes by viewModel.todayFocusMinutes.collectAsState(initial = 0)

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("Your Progress", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("Focus insights from your sessions", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Today", "${todayMinutes ?: 0} min", Modifier.weight(1f))
            StatCard("Sessions", "${sessions.size}", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        StatCard("Completed", "${sessions.count { it.completed }}", Modifier.fillMaxWidth())

        Spacer(Modifier.height(28.dp))
        Text("Recent", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))

        if (sessions.isEmpty()) {
            Text("No sessions yet. Start a focus block to see your history.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        } else {
            sessions.take(6).forEach { s ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(s.activityTag, fontWeight = FontWeight.Medium)
                        Text("${s.focusMinutes} min · ${if (s.completed) "Done" else "Partial"}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}
