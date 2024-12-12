@file:OptIn(ExperimentalTextApi::class)

package com.ovidium.comoriod.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.ovidium.comoriod.data.article.ArticleResponseChunk
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.ui.theme.getColorsForMarkup
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.unaccent
import java.time.Duration

val ParagraphStyle = SpanStyle(letterSpacing = 0.3.sp)

fun articulate(cnt: Int, many: String, single: String, isShort: Boolean = false): String {
    return "$cnt " + when {
        cnt == 0 -> many
        cnt == 1 -> single
        cnt > 20 -> if (isShort) many else "de $many"
        else -> many
    }
}

fun fmtDuration(duration: Duration, prefix: String = "acum"): String {
    val durations = listOf(
        Pair(duration.toDays() / 365, Pair("ani", "an")),
        Pair(duration.toDays() / 30, Pair("luni", "lună")),
        Pair(duration.toDays(), Pair("zile", "zi")),
        Pair(duration.toHours(), Pair("ore", "oră")),
        Pair(duration.toMinutes(), Pair("minute", "minut"))
    )

    for ((cnt, units) in durations) {
        val (multiple, single) = units
        if (cnt > 0)
            return "$prefix ${articulate(cnt.toInt(), multiple, single)}"
    }

    return prefix
}

fun highlightElements(text: String, isDark: Boolean): AnnotatedString {
    val highlightColor = getNamedColor("Highlight", isDark)

    return buildAnnotatedString {
        val parts = text.split("<em>", "</em>")

        withStyle(style = ParagraphStyle) {
            var highlighted = false
            for (part in parts) {
                if (highlighted) {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
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

fun highlightBody(text: String, isDark: Boolean): AnnotatedString {
    val textColor = getNamedColor("Text", isDark)

    return buildAnnotatedString {
        val parts = text.split("<em>", "</em>")

        withStyle(style = ParagraphStyle) {
            var highlighted = false
            for (part in parts) {
                if (highlighted) {
                    withStyle(
                        style = SpanStyle(
                            color = textColor,
                            background = Color.Transparent
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

fun fmtVerses(verses: List<String>, isDark: Boolean): List<AnnotatedString> {
    return verses.map { verse -> highlightElements(verse, isDark = isDark) }
}

fun parseVerses(
    verses: List<List<ArticleResponseChunk>>,
    markups: List<Markup>,
    highlights: SnapshotStateList<TextRange>,
    currentHighlightIndex: Int?,
    isDark: Boolean,
    clickedRef: MutableState<String>
): AnnotatedString {
    val linkColor = getNamedColor("Link", isDark)

    val bibleRefsRanges = ArrayList<Pair<Int, Int>>();
    fun buildChunk(index: Int, chunk: ArticleResponseChunk): AnnotatedString {
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
                        append(highlightBody(chunk.text, isDark))
                    }
                }

                "bible-ref" -> {
                    val link = LinkAnnotation.Url(
                        chunk.ref!!,
                        TextLinkStyles(SpanStyle(getNamedColor("Link", isDark)))
                    ) {
                        val url = (it as LinkAnnotation.Url).url
                        clickedRef.value = url
                    }
                    withLink(link) {
                        bibleRefsRanges.add(Pair(index + this.length, index + chunk.text.length))
                        append(highlightBody(chunk.text, isDark))
                    }
                }
            }
        }
    }

    fun buildVerse(index: Int, verse: List<ArticleResponseChunk>): AnnotatedString {
        return buildAnnotatedString {
            for (chunk in verse)
                append(buildChunk(index + this.length, chunk))

            append('\n')
        }
    }

    return buildAnnotatedString {
        val highlightColor = getNamedColor("Highlight", isDark)
        val activeHighlightColor = getNamedColor("ActiveHighlight", isDark)
        for (verse in verses)
            append(buildVerse(this.length, verse))

        for (markup in markups) {
            val (textColor, bgColor) = getColorsForMarkup(markup.bgColor)

            println("Markup: ${markup.index}..<${markup.index + markup.length}")
            addStyle(
                SpanStyle(
                    color = textColor,
                    background = bgColor
                ),
                start = markup.index,
                end = markup.index + markup.length
            )
        }

        for (hl in highlights) {
            println("Highlight: ${hl.start}..<${hl.end}")
            if (currentHighlightIndex != null && hl == highlights[currentHighlightIndex]) {
                addStyle(
                    SpanStyle(
                        color = Color.Black,
                        background = activeHighlightColor
                    ),
                    start = hl.start,
                    end = hl.end
                )
            } else {
                addStyle(
                    SpanStyle(
                        color = Color.Black,
                        background = highlightColor
                    ),
                    start = hl.start,
                    end = hl.end
                )
            }
        }

        for (range in bibleRefsRanges)
            addStyle(SpanStyle(color = linkColor), range.first, range.second)
    }
}


fun formatBibleRefs(item: BibleRefVerse, isDark: Boolean): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = getNamedColor("BibleRefBlue", isDark = isDark),
                fontWeight = FontWeight.SemiBold
            ),
        ) {
            append(item.name)
        }
        if (item.text.isNotEmpty()) {
            append(" - " + item.text)
        }
    }
}

fun highlightText(text: String, query: String, isDark: Boolean): AnnotatedString {
    val highlightColor = getNamedColor("Highlight", isDark)
    val rawText = text.unaccent().lowercase()

    val ranges: MutableList<Pair<Int, Int>> = mutableListOf()
    val parts = rawText.split(query.unaccent().lowercase())
    var currentIndex = 0
    for (part in parts) {
        currentIndex += part.length
        ranges.add(Pair(currentIndex, currentIndex + query.length))
        currentIndex += query.length
    }

    return buildAnnotatedString {
        append(text)
        for (range in ranges) {
            addStyle(SpanStyle(color = Color.Black, background = highlightColor), range.first, range.second)
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
