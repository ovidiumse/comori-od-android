package com.ovidium.comoriod.ui.theme

import androidx.compose.ui.graphics.Color

object colors {
    val colorPrimary = Color(0xFF08569B)
    val colorLightPrimary = Color(0xFFEEEEEE)
    val colorDarkPrimary = Color(0xFF414141)
    val colorSecondary =  Color(0xff9ccc65)
    val colorLightSecondary = Color(0xffcfff95)
    val colorDarkSecondary = Color(0xff6b9b37)
    val colorPrimaryText = Color(0xffffffff)
    val colorSecondaryText = Color(0xff000000)
}

private val LightNamedColors = mapOf(
    "Alice" to Color(0xFFC9D7E3),
    "Azure" to Color(0xFFC5EDFC),
    "Baby" to Color(0xFFE0E7DA),
    "Beige" to Color(0xFFDFDFB9),
    "Coral" to Color(0xFFFBC4B1),
    "CornSilk" to Color(0xFFF9EBB4),
    "Cultured" to Color(0xFFDDE0E3),
    "Floral" to Color(0xFFF6EDCB),
    "Lavender" to Color(0xFFF6CBDA),
    "Magnolia" to Color(0xFFDDDAE7),
    "Mint" to Color(.773f, .824f, .824f),
    "Prpl" to Color(0xFFE8DFFF),
    "Skye" to Color(0xFFDDEFF9),
    "Snow" to Color(0xFFEDD4E0),
    "Highlight" to Color(0xFFADE8F4),
    "Container" to Color(0xFFECECEC),
    "Link" to Color(0xFF08569B),
    "BibleRefBlue" to Color(0xFF90E0EF),
    "PopupContainer" to Color(0xFF393939),
    "InvertedText" to Color(0xFFF0EFF2),
    "CardButton" to Color(0xFFA6CBDD)
)

private val DarkNamedColors = mapOf(
    "Alice" to Color(0xFFE4EBF1),
    "Azure" to Color(0xFFEBF9FE),
    "Baby" to Color(0xFFF5F7F3),
    "Beige" to Color(0xFFEBEBD3),
    "Coral" to Color(0xFFFDE1D8),
    "CornSilk" to Color(0xFFFCF5D9),
    "Cultured" to Color(0xFFF4F5F6),
    "Floral" to Color(0xFFFCF9ED),
    "Lavender" to Color(0xFFFCEEF3),
    "Magnolia" to Color(0xFFF4F3F7),
    "Mint" to Color(.922f, .990f, .983f),
    "Prpl" to Color(0xFFE9E7FF),
    "Skye" to Color(0xFFE8F3FF),
    "Snow" to Color(0xFFF9F1F5),
    "Highlight" to Color(0xFFCAF0F8),
    "Container" to Color(0xFF3A3A3A),
    "Link" to Color(0xFF198CF3),
    "BibleRefBlue" to Color(0xFF0077B6),
    "PopupContainer" to Color(0xFFDEDEDE),
    "InvertedText" to Color(0xFF313131),
    "CardButton" to Color(0xFFA6CBDD)
)

fun getNamedColor(name: String, isDark: Boolean): Color? {
    return when(isDark) {
        true -> DarkNamedColors
        false -> LightNamedColors
    }[name]
}