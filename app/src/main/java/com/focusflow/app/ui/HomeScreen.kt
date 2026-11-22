package com.focusflow.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
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
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("FocusFlow", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))

        Canvas(modifier = Modifier.size(260.dp)) {
            val stroke = 16.dp.toPx()
            drawArc(Color.LightGray, -90f, 360f, false, style = Stroke(stroke, cap = StrokeCap.Round),
                topLeft = Offset(stroke / 2, stroke / 2), size = Size(size.width - stroke, size.height - stroke))
            drawArc(if (state.isFocusPhase) Color(0xFF6750A4) else Color(0xFF625B71), -90f, 360f * state.progress, false,
                style = Stroke(stroke, cap = StrokeCap.Round), topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(size.width - stroke, size.height - stroke))
        }

        Spacer(Modifier.height(16.dp))
        Text(formatTime(state.remainingSeconds), fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Text(if (state.isFocusPhase) "Focus · ${state.currentTag}" else "Break", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Study", "Code", "Read", "Write").forEach { tag ->
                FilterChip(selected = state.currentTag == tag, onClick = { viewModel.setTag(tag) }, label = { Text(tag) })
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(onClick = { viewModel.startOrPause() }, modifier = Modifier.fillMaxWidth(0.75f).height(56.dp)) {
            Text(if (state.isRunning) "Pause" else "Start")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { viewModel.reset() }) { Text("Reset") }
        Spacer(Modifier.height(16.dp))
        Text("Completed: ${state.completedSessions}")
    }
}

private fun formatTime(seconds: Int): String = "%02d:%02d".format(seconds / 60, seconds % 60)
