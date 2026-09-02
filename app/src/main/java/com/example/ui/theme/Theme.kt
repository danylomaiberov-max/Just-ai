package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = DarkVoid,
    primaryContainer = DarkSurface3,
    onPrimaryContainer = CyanNeon,
    secondary = PurpleNeon,
    onSecondary = Color.White,
    secondaryContainer = DarkSurface2,
    onSecondaryContainer = PurpleNeon,
    tertiary = EmeraldAi,
    onTertiary = DarkVoid,
    background = DarkVoid,
    onBackground = TextPrimary,
    surface = DarkSurface1,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurface2,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    error = CoralError,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme(
    primary = PurpleDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = PurpleDark,
    secondary = CyanNeon,
    onSecondary = DarkVoid,
    tertiary = EmeraldAi,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = CoralError,
    onError = Color.White
)

@Composable
fun AetherAITheme(
    darkTheme: Boolean = true, // Default to futuristic dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
