package com.actum.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ActumPrimarySoft,
    onPrimary = ActumWhite,

    secondary = ActumSecondarySoft,
    onSecondary = ActumWhite,

    tertiary = ActumPrimary,
    onTertiary = ActumWhite,

    background = Color(0xFF101828),
    onBackground = ActumWhite,

    surface = Color(0xFF182230),
    onSurface = ActumWhite,

    surfaceVariant = Color(0xFF243244),
    onSurfaceVariant = Color(0xFFD6DEEA),

    outline = Color(0xFF7C8BA1)
)

private val LightColorScheme = lightColorScheme(
    primary = ActumPrimary,
    onPrimary = ActumWhite,

    secondary = ActumSecondary,
    onSecondary = ActumWhite,

    tertiary = ActumPrimarySoft,
    onTertiary = ActumWhite,

    background = ActumBackground,
    onBackground = ActumTextPrimary,

    surface = ActumSurface,
    onSurface = ActumTextPrimary,

    surfaceVariant = ActumSurfaceSoft,
    onSurfaceVariant = ActumTextSecondary,

    outline = ActumBorder
)

@Composable
fun ActumTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}