package com.focusflow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple = Color(0xFF7C4DFF)
private val DeepPurple = Color(0xFF5E35B1)
private val SoftBackground = Color(0xFF0F0F14)
private val CardBackground = Color(0xFF1C1C24)

private val DarkColors = darkColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = Color(0xFFB388FF),
    background = SoftBackground,
    surface = CardBackground,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColors = lightColorScheme(
    primary = DeepPurple,
    onPrimary = Color.White,
    background = Color(0xFFF8F7FC),
    surface = Color.White
)

@Composable
fun FocusFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
