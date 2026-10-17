package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VibezColorScheme = darkColorScheme(
    primary = VibezNeonPink,
    onPrimary = Color.White,
    primaryContainer = Color(0x33FE2C55),
    onPrimaryContainer = Color.White,
    secondary = VibezCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0x3300F2FE),
    onSecondaryContainer = Color.White,
    tertiary = VibezViolet,
    onTertiary = Color.White,
    background = VibezBlack,
    onBackground = VibezTextPrimary,
    surface = VibezDarkSurface,
    onSurface = VibezTextPrimary,
    surfaceVariant = VibezCardBackground,
    onSurfaceVariant = VibezTextSecondary,
    outline = VibezCardBorder,
    outlineVariant = Color(0xFF3F3F4E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Vibez is immersive dark-mode-first
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VibezColorScheme,
        typography = Typography,
        content = content
    )
}
