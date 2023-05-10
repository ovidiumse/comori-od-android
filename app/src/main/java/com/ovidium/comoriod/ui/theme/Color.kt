package com.ovidium.comoriod.ui.theme

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

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
    "Beige" to Color(0xFFECEC76),
    "Coral" to Color(0xFFFBC4B1),
    "CornSilk" to Color(0xFFFFA585),
    "Cultured" to Color(0xFFFFEDA0),
    "Floral" to Color(0xFFF6EDCB),
    "Lavender" to Color(0xFFF6CBDA),
    "Magnolia" to Color(0xFFC8F7F4),
    "Mint" to Color(0xFF97A0FF),
    "Prpl" to Color(0xFFE8DFFF),
    "Skye" to Color(0xFFDDEFF9),
    "Snow" to Color(0xFFEDD4E0),
    "Highlight" to Color(0xFFFFF2CC),
    "ActiveHighlight" to Color(0xFFFFE599),
    "Container" to Color(0xFFF3F3F6),
    "Button" to Color(0xFF0969da),
    "Link" to Color(0xFF0969da),
    "BibleRefBlue" to Color(0xFF90E0EF),
    "HandleColor" to Color(0xFF90E0EF),
    "PopupContainer" to Color(0xFF393939),
    "InvertedText" to Color(0xFFF0EFF2),
    "Text" to Color(0xFF1B1B1B),
    "MutedText" to Color(0xff57606a),
    "CardButton" to Color(0xFFBECBD1),
    "HeaderBar" to Color(0xFFECECEC),
    "HeaderText" to Color(0xFF24292F),
    "Background" to Color(0xffffffff),
    "Border" to Color(0xFFE9EBF1),
    "OnBackground" to Color(0xff24292f),
    "DarkBar" to Color(0xff232830),
    "SecondaryBackground" to Color(0xff383F4D),
    "PrimarySurface" to Color(0xFFE7E9F1),
    "SecondarySurface" to Color(0xFF7F8796),
    "Bubble" to Color(0xFFD6DAE4),
    //Markup colors
    "markupChoc" to Color(0xFFFCA49E),
    "markupCrayola" to Color(0xFFFDD291),
    "markupCream" to Color(0xFFF8FC9C),
    "markupMorn" to Color(0xFFB2FDA2),
    "markupPers" to Color(0xFFFFC6FF),
    "markupSkye" to Color(0xFF9BF6FF),
    "markupSlate" to Color(0xFFACCBFF),
    "markupMauve" to Color(0xFFCAC1FD),

    "doneColor" to Color(0xFF26C485)
)

private val DarkNamedColors = mapOf(
    "Alice" to Color(0xFFE4EBF1),
    "Azure" to Color(0xFFEBF9FE),
    "Baby" to Color(0xFFF5F7F3),
    "Beige" to Color(0xFFEBEBD3),
    "Coral" to Color(0xFFFDE1D8),
    "CornSilk" to Color(0xFFFFA585),
    "Cultured" to Color(0xFFFFEDA0),
    "Floral" to Color(0xFFFCF9ED),
    "Lavender" to Color(0xFFFCEEF3),
    "Magnolia" to Color(0xFF9BF8F4),
    "Mint" to Color(0xFF6F7BF7),
    "Prpl" to Color(0xFFE9E7FF),
    "Skye" to Color(0xFFE8F3FF),
    "Snow" to Color(0xFFF9F1F5),
    "Highlight" to Color(0xFFFFF2CC),
    "ActiveHighlight" to Color(0xFFFFE599),
    "Container" to Color(0xFF242830),
    "Button" to Color(0xFF064B9C),
    "Link" to Color(0xff58a6ff),
    "BibleRefBlue" to Color(0xff58a6ff),
    "HandleColor" to Color(0xff58a6ff),
    "PopupContainer" to Color(0xFFDEDEDE),
    "InvertedText" to Color(0xFF313131),
    "Text" to Color(0xFFD7DCE2),
    "MutedText" to Color(0xFFA7B0B9),
    "CardButton" to Color(0xFFA6CBDD),
    "HeaderText" to Color(0xfff0f6fc),
    "HeaderBar" to Color(0xff161b22),
    "Background" to Color(0xff0d1117),
    "Border" to Color(0xFF242F3F),
    "OnBackground" to Color(0xffc9d1d9),
    "DarkBar" to Color(0xff232830),
    "SecondaryBackground" to Color(0xff383F4D),
    "PrimarySurface" to Color(0xFF2E3641),
    "SecondarySurface" to Color(0xFF707D99),
    "Bubble" to Color(0xff383F4D),
    //Markup colors
    "markupChoc" to Color(0xFFFDC1C1),
    "markupCrayola" to Color(0xFFFFD6A5),
    "markupCream" to Color(0xFFFAF3B3),
    "markupMorn" to Color(0xFFAFFF9E),
    "markupPers" to Color(0xFFFFC6FF),
    "markupSkye" to Color(0xFF9BF6FF),
    "markupSlate" to Color(0xFFACCBFF),
    "markupMauve" to Color(0xFFCAC1FD),

    "doneColor" to Color(0xFF26C485)
)

fun getNamedColor(name: String, isDark: Boolean): Color {
    Log.d("Color", "Getting color named $name")

    return when(isDark) {
        true -> DarkNamedColors
        false -> LightNamedColors
    }[name]!!
}

fun getColorsForMarkup(name: String): Pair<Color, Color> {
    val markupColor = getNamedColor(name, false)
    val textColor = Color(ColorUtils.blendARGB(markupColor.toArgb(), Color.Black.toArgb(), 0.9f))
    val bgColor = Color(ColorUtils.blendARGB(markupColor.toArgb(), Color.White.toArgb(), 0.6f))

    return Pair(textColor, bgColor)
}