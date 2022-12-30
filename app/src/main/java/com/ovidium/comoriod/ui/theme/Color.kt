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
    "Highlight" to Color(0xFFFFF2CC),
    "ActiveHighlight" to Color(0xFFFFE599),
    "Container" to Color(0xFFF3F3F6),
    "Link" to Color(0xFF0969da),
    "BibleRefBlue" to Color(0xFF90E0EF),
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
    "markupChoc" to Color(0xFFF39453),
    "markupCrayola" to Color(0xFFFDD05D),
    "markupMorn" to Color(0xFF0CE5AB),
    "markupPers" to Color(0xFFC690FF),
    "markupSkye" to Color(0xFF30B5FD),
    "markupSlate" to Color(0xFFBEBCC0)
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
    "Highlight" to Color(0xFFFFF2CC),
    "ActiveHighlight" to Color(0xFFFFE599),
    "Container" to Color(0xFF3A3A3A),
    "Link" to Color(0xff58a6ff),
    "BibleRefBlue" to Color(0xff58a6ff),
    "PopupContainer" to Color(0xFFDEDEDE),
    "InvertedText" to Color(0xFF313131),
    "Text" to Color(0xffc9d1d9),
    "MutedText" to Color(0xff8b949e),
    "CardButton" to Color(0xFFA6CBDD),
    "HeaderText" to Color(0xfff0f6fc),
    "HeaderBar" to Color(0xff161b22),
    "Background" to Color(0xff0d1117),
    "Border" to Color(0xFF242F3F),
    "OnBackground" to Color(0xffc9d1d9),
    "DarkBar" to Color(0xff232830),
    "SecondaryBackground" to Color(0xff383F4D),
    "PrimarySurface" to Color(0xFF252B33),
    "SecondarySurface" to Color(0xFF707D99),
    "Bubble" to Color(0xff383F4D),
    //Markup colors
    "markupChoc" to Color(0xFFF1DBCC),
    "markupCrayola" to Color(0xFFFAE6B3),
    "markupMorn" to Color(0xFF26F7BF),
    "markupPers" to Color(0xFFDFC5FB),
    "markupSkye" to Color(0xFF99DBFF),
    "markupSlate" to Color(0xFFE0DFE2)
)

fun getNamedColor(name: String, isDark: Boolean): Color {
    return when(isDark) {
        true -> DarkNamedColors
        false -> LightNamedColors
    }[name]!!
}