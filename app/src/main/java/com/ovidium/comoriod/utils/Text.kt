package com.ovidium.comoriod.utils

import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.ovidium.comoriod.data.article.ArticleResponseChunk
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.ui.theme.colors.colorSecondaryText
import com.ovidium.comoriod.ui.theme.getNamedColor
import java.text.Normalizer

fun articulate(cnt: Int, many: String, single: String, isShort: Boolean = false) : String {
    return "$cnt " + when {
        cnt == 0 -> many
        cnt == 1 -> single
        cnt > 20 -> if (isShort) many else "de $many"
        else -> many
    }
}

fun highlightText(text: String, isDark: Boolean): AnnotatedString {
    val highlightColor = getNamedColor("Highlight", isDark)

    return buildAnnotatedString {
        val parts = text.split("<em>", "</em>")

        withStyle(style=SpanStyle(letterSpacing = 0.3.sp)) {
            var highlighted = false
            for (part in parts) {
                if (highlighted) {
                    withStyle(
                        style = SpanStyle(
                            color = colorSecondaryText,
                            background = highlightColor
                        ),
                    ) {
                        append(part)
                    }
                } else
                    append(part)

                highlighted = !highlighted
            }
        }
    }
}

fun fmtVerses(verses: List<String>, isDark: Boolean): AnnotatedString {
    return highlightText(verses.joinToString(separator = "\n"), isDark = isDark)
}

fun parseVerses(verses: List<List<ArticleResponseChunk>>, markups: List<Markup>, isDark: Boolean) : AnnotatedString {
    fun buildChunk(chunk: ArticleResponseChunk): AnnotatedString {
        fun buildStyle(styles: List<String>): SpanStyle {
            var style = SpanStyle(letterSpacing = 0.3.sp)
            for (s in styles) {
                when (s) {
                    "italic" -> style = style.merge(SpanStyle(fontStyle = FontStyle.Italic))
                    "bold" -> style = style.merge(SpanStyle(fontWeight = FontWeight.Bold))
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
                    withAnnotation(tag = "URL",  annotation = chunk.ref!!) {
                        withStyle(style = SpanStyle(color = getNamedColor("Link", isDark))) {
                            append(chunk.text)
                        }
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
        for (markup in markups) {
            addStyle(
                SpanStyle(
                    color = getNamedColor("InvertedText", isDark),
                    background = getNamedColor(markup.bgColor, isDark)
                ),
                start = markup.index,
                end = markup.index + markup.length
            )
        }
    }
}


fun formatBibleRefs(item: BibleRefVerse, isDark: Boolean): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = getNamedColor("BibleRefBlue", isDark = isDark),
                fontWeight = FontWeight.Bold
            ),
        ) {
            append(item.name)
        }
        if (item.text.isNotEmpty()) {
            append(" - " + item.text)
        }
    }
}

fun String.normalize(): String {
    return this
        .replace("ș", "s")
        .replace("ț", "t")
        .replace("ă", "a")
        .replace("î", "i")
        .replace("â", "a")
        .replace("ţ", "t")
        .replace("ş", "s")
}
