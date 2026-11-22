package com.focusflow.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusflow.app.R
import com.focusflow.app.viewmodel.PomodoroViewModel

@Composable
fun HomeScreen(viewModel: PomodoroViewModel = viewModel()) {
    val state by viewModel.timerState.collectAsState()

    val progressSpring = spring<Float>(dampingRatio = 0.8f, stiffness = 380f)
    val buttonSpring = spring<Float>(dampingRatio = 0.6f, stiffness = 800f)
    val chipSpring = spring<Float>(dampingRatio = 0.7f, stiffness = 500f)

    val animatedProgress by animateFloatAsState(targetValue = state.progress, animationSpec = progressSpring, label = "timerProgress")

    val startInteraction = remember { MutableInteractionSource() }
    val isStartPressed by startInteraction.collectIsPressedAsState()
    val startScale by animateFloatAsState(targetValue = if (isStartPressed) 0.95f else 1f, animationSpec = buttonSpring, label = "startScale")

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.ic_logo), contentDescription = "FocusFlow logo", modifier = Modifier.size(72.dp).clip(CircleShape))
        Spacer(Modifier.height(12.dp))
        Text("FocusFlow", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Deep work, fewer distractions", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))

        Spacer(Modifier.height(32.dp))

        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(260.dp)) {
                val stroke = 16.dp.toPx()
                val diameter = size.minDimension - stroke
                val topLeft = Offset(stroke / 2, stroke / 2)
                drawArc(Color.White.copy(alpha = 0.1f), -90f, 360f, false, style = Stroke(stroke, cap = StrokeCap.Round), topLeft = topLeft, size = Size(diameter, diameter))
                drawArc(if (state.isFocusPhase) Color(0xFF7C4DFF) else Color(0xFF26A69A), -90f, 360f * animatedProgress, false, style = Stroke(stroke, cap = StrokeCap.Round), topLeft = topLeft, size = Size(diameter, diameter))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedContent(targetState = "%02d:%02d".format(state.remainingSeconds / 60, state.remainingSeconds % 60), transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "time") { time ->
                    Text(time, fontSize = 52.sp, fontWeight = FontWeight.Bold)
                }
                Text(if (state.isFocusPhase) state.currentTag else "Break", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("Study", "Code", "Read", "Write").forEach { tag ->
                val selected = state.currentTag == tag
                val chipScale by animateFloatAsState(targetValue = if (selected) 1.05f else 1f, animationSpec = chipSpring, label = "chip_$tag")
                FilterChip(selected = selected, onClick = { viewModel.setTag(tag) }, label = { Text(tag) }, modifier = Modifier.weight(1f).graphicsLayer { scaleX = chipScale; scaleY = chipScale })
            }
        }

        Spacer(Modifier.height(28.dp))

        Button(onClick = { viewModel.startOrPause() }, modifier = Modifier.fillMaxWidth().height(56.dp).graphicsLayer { scaleX = startScale; scaleY = startScale }, shape = RoundedCornerShape(20.dp), interactionSource = startInteraction) {
            Text(if (state.isRunning) "Pause" else "Start Focus", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = { viewModel.reset() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) { Text("Reset") }

        Spacer(Modifier.height(20.dp))

        if (!state.isRunning) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                TimeSetter(
                    label = if (state.isFocusPhase) "Focus" else "Break",
                    minutes = if (state.isFocusPhase) viewModel.getFocusMinutes() else viewModel.getBreakMinutes(),
                    onChange = { mins -> if (state.isFocusPhase) viewModel.setFocusMinutes(mins) else viewModel.setBreakMinutes(mins) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Sessions completed: ${state.completedSessions}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
    }
}

@Composable
private fun TimeSetter(label: String, minutes: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(12.dp))
        FilledTonalButton(onClick = { onChange(minutes - 1) }, enabled = minutes > 1) { Text("−") }
        Text("$minutes min", modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Medium)
        FilledTonalButton(onClick = { onChange(minutes + 1) }) { Text("+") }
    }
}
