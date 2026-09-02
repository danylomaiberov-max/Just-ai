package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(
    val id: String,
    val title: String,
    val description: String,
    val accentColor: Color,
    val isDark: Boolean = true
) {
    CRIMSON_NEON(
        id = "crimson_neon",
        title = "Красный Неон (Crimson)",
        description = "Фирменный неоновый алый стиль со светящимися рамками и графитом",
        accentColor = Color(0xFFFF2A55)
    ),
    CYBERPUNK_RUBY(
        id = "cyberpunk_ruby",
        title = "Киберпанк Рубин (Ruby)",
        description = "Глубокий рубиново-красный с тёмно-пурпурными акцентами",
        accentColor = Color(0xFFE52E53)
    ),
    OLED_RED(
        id = "oled_red",
        title = "OLED Черный и Красный",
        description = "Истинно черный фон #000000 для экономии батареи и яркий алый акцент",
        accentColor = Color(0xFFFF1744)
    ),
    DARK_SLATE(
        id = "dark_slate",
        title = "Тёмный Графит (Slate)",
        description = "Индустриальный сдержанный темно-серый стиль с синим акцентом",
        accentColor = Color(0xFF38BDF8)
    ),
    LIGHT_STUDIO(
        id = "light_studio",
        title = "Светлая Студия (Light)",
        description = "Чистый светлый интерфейс с алыми контрастными кнопками",
        accentColor = Color(0xFFE11D48),
        isDark = false
    );

    companion object {
        fun fromId(id: String): AppThemeMode {
            return entries.find { it.id == id } ?: CRIMSON_NEON
        }
    }
}

private val CrimsonNeonScheme = darkColorScheme(
    primary = CrimsonNeon,
    onPrimary = DarkVoid,
    primaryContainer = DarkSurface3,
    onPrimaryContainer = CrimsonNeon,
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

private val CyberpunkRubyScheme = darkColorScheme(
    primary = Color(0xFFE52E53),
    onPrimary = Color(0xFF130D12),
    primaryContainer = Color(0xFF2B1C28),
    onPrimaryContainer = Color(0xFFFF3B60),
    secondary = Color(0xFFFF3B60),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E141C),
    onSecondaryContainer = Color(0xFFFF5277),
    tertiary = Color(0xFFFF7043),
    onTertiary = Color.White,
    background = Color(0xFF130D12),
    onBackground = TextPrimary,
    surface = Color(0xFF1E141C),
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF2B1C28),
    onSurfaceVariant = TextSecondary,
    outline = Color(0x80E52E53),
    error = CoralError,
    onError = Color.White
)

private val OledRedScheme = darkColorScheme(
    primary = Color(0xFFFF1744),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF161618),
    onPrimaryContainer = Color(0xFFFF1744),
    secondary = Color(0xFFFF5252),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0C0C0D),
    onSecondaryContainer = Color(0xFFFF5252),
    tertiary = Color(0xFFFF8A80),
    onTertiary = Color.Black,
    background = Color(0xFF000000),
    onBackground = TextPrimary,
    surface = Color(0xFF0C0C0D),
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF161618),
    onSurfaceVariant = TextSecondary,
    outline = Color(0x66FF1744),
    error = CoralError,
    onError = Color.White
)

private val DarkSlateScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF334155),
    onPrimaryContainer = Color(0xFF38BDF8),
    secondary = Color(0xFF818CF8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFF818CF8),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF0F172A),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    error = CoralError,
    onError = Color.White
)

private val LightStudioScheme = lightColorScheme(
    primary = Color(0xFFE11D48),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEE2E2),
    onPrimaryContainer = Color(0xFF991B1B),
    secondary = Color(0xFF9333EA),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = Color(0xFF581C87),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun AetherAITheme(
    themeMode: AppThemeMode = AppThemeMode.CRIMSON_NEON,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.CRIMSON_NEON -> CrimsonNeonScheme
        AppThemeMode.CYBERPUNK_RUBY -> CyberpunkRubyScheme
        AppThemeMode.OLED_RED -> OledRedScheme
        AppThemeMode.DARK_SLATE -> DarkSlateScheme
        AppThemeMode.LIGHT_STUDIO -> LightStudioScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
