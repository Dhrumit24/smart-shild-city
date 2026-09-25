package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color(0xFF001B2E),
    primaryContainer = Color(0xFF00496E),
    onPrimaryContainer = Color(0xFFCBE6FF),
    secondary = IndigoIntelligence,
    onSecondary = Color(0xFF1B1B4F),
    secondaryContainer = Color(0xFF313175),
    onSecondaryContainer = Color(0xFFE0E0FF),
    tertiary = TealPulse,
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF005048),
    onTertiaryContainer = Color(0xFF70F7E7),
    background = CommandBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = CommandSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CommandCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CommandBorder,
    outlineVariant = CommandSurfaceHighlight,
    error = AlertCritical,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF6366F1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEF2FF),
    onSecondaryContainer = Color(0xFF4338CA),
    tertiary = Color(0xFF0D9488),
    onTertiary = Color.White,
    background = CommandBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = CommandSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = TextSecondaryLight,
    outline = CommandBorderLight,
    error = AlertCritical,
    onError = Color.White
)

@Composable
fun SmartShieldTheme(
    darkTheme: Boolean = true, // Command center default is sleek dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
