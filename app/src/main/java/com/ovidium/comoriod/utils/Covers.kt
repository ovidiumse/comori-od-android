package com.ovidium.comoriod.utils

import androidx.compose.ui.graphics.Color
import com.ovidium.comoriod.ui.theme.getNamedColor

fun getVolumeCoverGradient(name: String, isDark: Boolean): List<Color> {
    val gradientMapping = mapOf(
        "Cugetări Nemuritoare" to listOf("CornSilk", "Cultured"),
        "Meditații la Ev. după Ioan" to listOf("CornSilk", "Lavender"),
        "Spre Canaan" to listOf("Prpl", "Skye"),
        "Cântări Nemuritoare" to listOf("Alice", "Azure"),
        "Hristos - Puterea Apostoliei" to listOf("Magnolia", "Snow"),
        "Istoria unei Jertfe" to listOf("Mint", "Magnolia"),
        "Hristos - Marturia mea" to listOf("Beige", "Coral")
    )

    return when (val colorList = gradientMapping[name]) {
        null -> listOf("Lavender", "Snow")
        else -> colorList
    }.map { colorName -> getNamedColor(colorName, isDark)!! }
}