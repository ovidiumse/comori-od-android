package com.ovidium.comoriod.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = colors.colorDarkPrimary,
    primaryVariant = colors.colorLightPrimary,
    secondary = colors.colorSecondary,
    onPrimary = colors.colorPrimaryText,
    onSecondary = colors.colorSecondaryText
)

private val LightColorPalette = lightColors(
    primary = colors.colorDarkPrimary,
    primaryVariant = colors.colorLightPrimary,
    secondary = colors.colorLightSecondary,
    onPrimary = colors.colorPrimaryText,
    onSecondary = colors.colorSecondaryText

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ComoriODTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}