package com.ovidium.comoriod.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.ovidium.comoriod.data.article.ArticleResponseVerse
import com.ovidium.comoriod.data.article.VerseType
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
                        color = colorSecondaryText,
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


fun parseVerse(verses: List<List<ArticleResponseVerse>>, isDark: Boolean): AnnotatedString {
    return buildAnnotatedString {
        println(verses)
        for (verse in verses) {
            if (verse.isEmpty()) {
                append("\n")
            } else {
                if (verse.first().type == "normal") {
                    if (verse.first().style.isEmpty()) {
                        println("VERSE: ${verse.first().style.joinToString(", ")}")
                        append(verse.first().text + "\n")
                    } else {
                        println("VERSE: ${verse.first().style.joinToString(", ")}")
                        when (verse.first().style.joinToString(", ")) {
                            "italic" -> { withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) { append(verse.first().text) } }
                            "bold, italic" -> { withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)) { append(verse.first().text) } }
                            "bold" -> { withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(verse.first().text) } }
                        }
                    }
                } else if (verse.first().type == "bible-ref") {
                    withStyle(style = SpanStyle(color = getNamedColor("Link", isDark = isDark)!!)) { append(verse.first().text) }
                }
            }
        }
    }
}