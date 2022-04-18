package com.ovidium.comoriod.utils

//import androidx.compose.material.Colors
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.ovidium.comoriod.ui.theme.colors.colorPrimaryText
import com.ovidium.comoriod.ui.theme.colors.colorSecondaryText
import com.ovidium.comoriod.ui.theme.getNamedColor

fun articulate(cnt: Int, many: String, single: String) : String {
    return "$cnt " + when {
        cnt == 0 -> many
        cnt == 1 -> single
        cnt > 20 -> "de $many"
        else -> many
    }
}

fun highlightText(text: String, isDark: Boolean): AnnotatedString {
    return buildAnnotatedString {
        val parts = text.split("<em>", "</em>")

        var highlighted = false
        for (part in parts) {
            if (highlighted) {
                withStyle(
                    style = SpanStyle(
                        color = if (isDark) colorSecondaryText else colorPrimaryText,
                        background = getNamedColor("Highlight", isDark = isDark)!!),
                ) {
                    append(part)
                }
            } else
                append(part)

            highlighted = !highlighted
        }
    }
}

fun fmtVerses(verses: List<String>, isDark: Boolean): AnnotatedString {
    return highlightText(verses.joinToString(separator = "\n"), isDark = isDark)
}