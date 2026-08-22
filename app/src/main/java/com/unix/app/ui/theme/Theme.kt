package com.unix.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = CampusNavy700,
    onPrimary = Color_White,
    secondary = AmberSparkDeep,
    onSecondary = Color_White,
    tertiary = GrowthTealDeep,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondaryLight,
    error = AlertCoral,
)

private val DarkScheme = darkColorScheme(
    primary = AmberSpark,
    onPrimary = CampusNavy900,
    secondary = GrowthTeal,
    onSecondary = CampusNavy900,
    tertiary = GrowthTeal,
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CampusNavy800,
    onSurfaceVariant = TextSecondaryDark,
    error = AlertCoral,
)

@Composable
fun UniXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkScheme else LightScheme
    MaterialTheme(
        colorScheme = colors,
        typography = UniXTypography,
        content = content,
    )
}
