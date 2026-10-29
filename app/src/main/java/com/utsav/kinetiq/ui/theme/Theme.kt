package com.utsav.kinetiq.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = KinetiqCyan,
    onPrimary = KinetiqCosmicBlack,
    primaryContainer = KinetiqViolet,
    onPrimaryContainer = KinetiqWhite,
    secondary = KinetiqPurple,
    onSecondary = KinetiqWhite,
    tertiary = KinetiqMagenta,
    onTertiary = KinetiqWhite,
    background = KinetiqCosmicBlack,
    onBackground = KinetiqWhite,
    surface = KinetiqCharcoal,
    onSurface = KinetiqWhite,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = BorderGlass
)

@Composable
fun KinetiqTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = KinetiqCosmicBlack.toArgb()
            window.navigationBarColor = KinetiqCosmicBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
