package com.androidforge.streakhabit.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.androidforge.streakhabit.domain.model.AppTheme
import androidx.compose.material3.Shapes as M3Shapes

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = Secondary,
    onSecondary = OnPrimary,
    secondaryContainer = Secondary,
    onSecondaryContainer = OnPrimary,
    tertiary = Warning,
    onTertiary = OnPrimary,
    tertiaryContainer = Warning,
    onTertiaryContainer = OnPrimary,
    error = Error,
    onError = OnPrimary,
    errorContainer = Error,
    onErrorContainer = OnPrimary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurface,
    outline = SurfaceVariant,
    outlineVariant = SurfaceVariant,
    scrim = Color(0x99000000)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryVariant,
    onPrimaryContainer = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnPrimary,
    secondaryContainer = LightSecondary,
    onSecondaryContainer = LightOnPrimary,
    tertiary = LightWarning,
    onTertiary = LightOnPrimary,
    tertiaryContainer = LightWarning,
    onTertiaryContainer = LightOnPrimary,
    error = LightError,
    onError = LightOnPrimary,
    errorContainer = LightError,
    onErrorContainer = LightOnPrimary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
    outline = LightSurfaceVariant,
    outlineVariant = LightSurfaceVariant,
    scrim = Color(0x99000000)
)

val AppShapes = M3Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp), // Chip
    medium = RoundedCornerShape(8.dp), // Card, Button
    large = RoundedCornerShape(16.dp), // BottomSheet top corners
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun StreakHabitTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val currentDarkTheme = when (appTheme) {
        AppTheme.SYSTEM -> darkTheme
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (currentDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        currentDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb() // Set nav bar color as well
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !currentDarkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !currentDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}