package com.ovidium.comoriod.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.ovidium.comoriod.data.article.ArticleResponseChunk
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


fun parseVerses(verses: List<List<ArticleResponseChunk>>, isDark: Boolean) : AnnotatedString {
    fun buildChunk(chunk: ArticleResponseChunk): AnnotatedString {
        fun buildStyle(styles: List<String>): SpanStyle {
            val style = SpanStyle()
            for (s in styles) {
                when (s) {
                    "italic" -> style.merge(SpanStyle(fontStyle = FontStyle.Italic))
                    "bold" -> style.merge(SpanStyle(fontWeight = FontWeight.Bold))
                }
            }

            return style
        }

        return buildAnnotatedString {
            when (chunk.type) {
                "normal" -> {
                    withStyle(buildStyle(chunk.style)) {
                        append(chunk.text)
                    }
                }
                "bible-ref" -> {
                    withStyle(style = SpanStyle(color = getNamedColor("Link", isDark)!!)) {
                        append(chunk.text)
                    }
                }
            }
        }
    }

    fun buildVerse(verse: List<ArticleResponseChunk>): AnnotatedString {
        return buildAnnotatedString {
            for (chunk in verse)
                append(buildChunk(chunk))

            append('\n')
        }
    }

    return buildAnnotatedString {
        for (verse in verses)
            append(buildVerse(verse))
    }
}