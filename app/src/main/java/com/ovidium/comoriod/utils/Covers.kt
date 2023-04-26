package com.ovidium.comoriod.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.ovidium.comoriod.ui.theme.getNamedColor

fun getVolumeCoverGradient(name: String, isDark: Boolean): List<Color> {
    val gradientMapping = mapOf(
        "Cugetări Nemuritoare" to listOf(Color(0xFFFAF0C7), Color(0xFFFFAC85)),
        "Meditații la Ev. după Ioan" to listOf(Color(0xFFDDEFF9), Color(0xFFFFA585)),
        "Spre Canaan" to listOf(Color(0xFFCDC1EB), Color(0xFFBAE1F7)),
        "Cântări Nemuritoare" to listOf(Color(0xFFDEECFA), Color(0xFFF5CCD4), Color(0xFFB8A4C9)),
        "Hristos - Puterea Apostoliei" to listOf(Color(0xFFC8F0F7), Color(0xFFEDD4E0)),
        "Istoria unei Jertfe" to listOf(Color(0xFFA4BCF8), Color(0xFFEDFAF9), Color(0xFFC5D4FA)),
        "Hristos - Marturia mea" to listOf(Color(0xFFC4FC99), Color(0xFFF8B8B8)),
        "Culese din ziare" to listOf(Color(0xFFD7F5EF), Color(0xFF57C7C4)),
        "Ce este Oastea Domnului" to listOf(Color(0xFFFADEE2), Color(0xFFC8AFDB)),
        "Strângeți Fărâmiturile" to listOf(Color(0xFFF8ECE8), Color(0xFFE78A82))
    )

    val colorList = when (val colorList = gradientMapping[name]) {
        null -> listOf(Color(0xFFC8F7F4), Color(0xFFD9DCFF))
        else -> colorList
    }

    return colorList.map {color->if (isDark) Color(
        ColorUtils.blendARGB(
            color.toArgb(),
            Color.White.toArgb(),
            0.6f
        )
    ) else color.copy(0.7f)}
}

fun getAuthorCoverGradient(name: String, isDark: Boolean): List<Color> {
    val gradientMapping = mapOf(
        "Pr. Iosif Trifa" to listOf(Color(0xFFC0F3E9), Color(0xFFD6F3EE), Color(0xFF44B3B0)),
        "Traian Dorz" to listOf(Color(0xFFFAF0C7), Color(0xFFFCAC8D)),
        "Popa Petru (Săucani)" to listOf(Color(0xFF55A7C0), Color(0xFFEBF4F5), Color(0xFFABEAF1)),
        "Arcadie Nistor" to listOf(Color(0xFF57EBDE), Color(0xFFEFF8DF))
    )

    val colorList = when (val colorList = gradientMapping[name]) {
        null -> listOf(Color(0xFFD1E7F7), Color(0xFFA6E7D9), Color(0xFFC8E6E7))
        else -> colorList
    }

    return colorList.map { color ->
        if (isDark) Color(
            ColorUtils.blendARGB(
                color.toArgb(),
                Color.White.toArgb(),
                0.6f
            )
        ) else color.copy(0.5f)
    }
}