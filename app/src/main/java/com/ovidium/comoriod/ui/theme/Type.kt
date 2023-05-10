package com.ovidium.comoriod.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.R

val headingFonts = FontFamily(
    Font(R.font.sourcesanspro_regular),
    Font(R.font.sourcesanspro_bold, weight = FontWeight.Bold),
    Font(R.font.sourcesanspro_light, weight = FontWeight.Light),
    Font(R.font.sourcesanspro_extralight, weight = FontWeight.Thin),
    Font(R.font.sourcesanspro_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.sourcesanspro_bolditalic, weight = FontWeight.Bold, style = FontStyle.Italic)
)

val textFonts = FontFamily(
    Font(R.font.sourceserifpro_regular),
    Font(R.font.sourceserifpro_bold, weight = FontWeight.Bold),
    Font(R.font.sourceserifpro_light, weight = FontWeight.Light),
    Font(R.font.sourceserifpro_extralight, weight = FontWeight.Thin),
    Font(R.font.sourceserifpro_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.sourceserifpro_blackitalic, weight = FontWeight.Bold, style = FontStyle.Italic)
)

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = headingFonts,
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),

    h2 = TextStyle(
        fontFamily = headingFonts,
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
        letterSpacing = (-0.5).sp
    ),

    h3 = TextStyle(
        fontFamily = headingFonts,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        letterSpacing = 0.sp
    ),

    h4 = TextStyle(
        fontFamily = headingFonts,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        letterSpacing = 0.25.sp
    ),
    h5 = TextStyle(
        fontFamily = headingFonts,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),

    h6 = TextStyle(
        fontFamily = headingFonts,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),

    subtitle1 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),

    subtitle2 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),

    body1 = TextStyle(
        fontFamily = textFonts,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),

    body2 = TextStyle(
        fontFamily = textFonts,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),

    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp
    ),

    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    )
)