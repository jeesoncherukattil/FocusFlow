package com.focusflow.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusflow.app.viewmodel.PomodoroViewModel

@Composable
fun HomeScreen(viewModel: PomodoroViewModel = viewModel()) {
    val state by viewModel.timerState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("FocusFlow", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        Text("Deep work, fewer distractions", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(40.dp))

        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(280.dp)) {
                val stroke = 18.dp.toPx()
                val diameter = size.minDimension - stroke
                val topLeft = Offset(stroke / 2, stroke / 2)
                drawArc(Color.White.copy(alpha = 0.12f), -90f, 360f, false, style = Stroke(stroke, cap = StrokeCap.Round), topLeft = topLeft, size = Size(diameter, diameter))
                drawArc(if (state.isFocusPhase) Color(0xFF7C4DFF) else Color(0xFF26A69A), -90f, 360f * state.progress, false, style = Stroke(stroke, cap = StrokeCap.Round), topLeft = topLeft, size = Size(diameter, diameter))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formatTime(state.remainingSeconds), fontSize = 56.sp, fontWeight = FontWeight.Bold)
                Text(if (state.isFocusPhase) state.currentTag else "Break", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(36.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("Study", "Code", "Read", "Write").forEach { tag ->
                FilterChip(selected = state.currentTag == tag, onClick = { viewModel.setTag(tag) }, label = { Text(tag) }, modifier = Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(onClick = { viewModel.startOrPause() }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Text(if (state.isRunning) "Pause" else "Start Focus", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { viewModel.reset() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Reset") }

        Spacer(Modifier.height(16.dp))
        Text("Focus active flag: ${com.focusflow.app.service.ReelInterceptionService.isFocusActive}", style = MaterialTheme.typography.bodySmall)
        Text("Blocked packages: ${com.focusflow.app.service.BlockedAppsStore.packages.collectAsState().value.size}", style = MaterialTheme.typography.bodySmall)
        Text("Sessions today: ${state.completedSessions}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

private fun formatTime(seconds: Int): String = "%02d:%02d".format(seconds / 60, seconds % 60)
