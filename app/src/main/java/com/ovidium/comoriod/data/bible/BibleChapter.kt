package com.ovidium.comoriod.data.bible

import android.graphics.fonts.FontStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp
import com.google.gson.annotations.SerializedName
import com.ovidium.comoriod.ui.theme.Montserrat
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.addSuffix
import com.ovidium.comoriod.utils.toSpanStyle

data class BibleChapter(
    val took: Int,
    @SerializedName("timed_out") val timedOut: Boolean,
    val hits: BibleHits,
    @SerializedName("bible-refs") val bibleRefs: Map<String, BibleRef>?,
    @SerializedName("od-refs") val odRefs: Map<String, ODRef>?
) {
    companion object {

        fun BibleChapter.getFormatedText(isDarkMode: Boolean): List<BibleVerse> {

            val finalBibleVerseList = mutableListOf<BibleVerse>()
            val verses: List<Verse> = this.hits.hits.flatMap { it.source.verses }

            val verseNumberStyle = SpanStyle(color = Color.Gray, fontSize = 20.sp)
            val starSymbolStyle = SpanStyle(color = getNamedColor("markupChoc", isDarkMode), fontSize = 14.sp)
            val reverseRefStyle = SpanStyle(color = getNamedColor("markupChoc", isDarkMode), fontSize = 18.sp, baselineShift = BaselineShift(0.25f))
            val verseReferenceStyle = SpanStyle(color = getNamedColor("Link", isDarkMode), fontSize = 16.sp)

            verses.forEach { verse ->

                // build the verse with reference symbols
                val annotatedString = buildAnnotatedString {

                    append(verse.number.toString().addSuffix(DOT_SYMBOL).toSpanStyle(verseNumberStyle))
                    append(SPACE_SYMBOL)

                    verse.content.forEach { verse ->
                        append(verse.text.toSpanStyle(getContentStyleBy(verse.type, isDarkMode)))
                    }
                }

                // build the verse references with reference symbols
                val annotatedRef = buildAnnotatedString {
                    verse.references.oneStar?.forEach {
                        append(ONE_STAR_SYMBOL.toSpanStyle(starSymbolStyle))
                        append(
                            it.addSuffix(SPACE_SYMBOL).addSuffix(SPACE_SYMBOL).toSpanStyle(verseReferenceStyle, AnnotationString(it, "URL"))
                        )
                    }

                    verse.references.twoStars?.forEach {
                        append(TWO_STARS_SYMBOL.toSpanStyle(starSymbolStyle))
                        append(
                            it.addSuffix(SPACE_SYMBOL).addSuffix(SPACE_SYMBOL).toSpanStyle(verseReferenceStyle, AnnotationString(it, "URL"))
                        )
                    }

                    verse.references.oneCross?.forEach {
                        append(ONE_CROSS_SYMBOL.toSpanStyle(starSymbolStyle))
                        append(
                            it.addSuffix(SPACE_SYMBOL).addSuffix(SPACE_SYMBOL).toSpanStyle(verseReferenceStyle, AnnotationString(it, "URL"))
                        )
                    }

                    verse.references.twoCrosses?.forEach {
                        append(TWO_CROSSES_SYMBOL.toSpanStyle(starSymbolStyle))
                        append(
                            it.addSuffix(SPACE_SYMBOL).addSuffix(SPACE_SYMBOL).toSpanStyle(verseReferenceStyle, AnnotationString(it, "URL"))
                        )
                    }

                    verse.references.starAndCross?.forEach {
                        append(ONE_STAR_ONE_CROSS_SYMBOL.toSpanStyle(starSymbolStyle))
                        append(
                            it.addSuffix(SPACE_SYMBOL).addSuffix(SPACE_SYMBOL).toSpanStyle(verseReferenceStyle, AnnotationString(it, "URL"))
                        )
                    }

                    if (!verse.reverseReferences.isNullOrEmpty()) {
                        if (
                            !verse.references.oneStar.isNullOrEmpty() ||
                            !verse.references.twoStars.isNullOrEmpty() ||
                            !verse.references.oneCross.isNullOrEmpty() ||
                            !verse.references.twoCrosses.isNullOrEmpty() ||
                            !verse.references.starAndCross.isNullOrEmpty()
                            ) {
                            append(NEW_LINE_SYMBOL.toSpanStyle(verseReferenceStyle))
                        }
                        append(REV_REF_SYMBOL.toSpanStyle(reverseRefStyle))
                        verse.reverseReferences.forEach {
                            append(
                                it.addSuffix(SPACE_SYMBOL).addSuffix(SPACE_SYMBOL).addSuffix(SPACE_SYMBOL)
                                    .toSpanStyle(verseReferenceStyle, AnnotationString(it, "URL"))
                            )
                        }
                    }

                }

                finalBibleVerseList.add(
                    BibleVerse(
                        annotatedString, annotatedRef, verse.odRefs
                    )
                )
            }
            return finalBibleVerseList
        }

        fun BibleChapter.getFormatedReferencesForVerse(verse: String, isDarkMode: Boolean): AnnotatedString {

            val verseTitleStyle = SpanStyle(color = getNamedColor("Link", isDarkMode), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            val normalContentStyle = SpanStyle(fontSize = 18.sp)

            val refVerses = this.bibleRefs?.mapValues { it.value.verses }
            // build the verse with reference symbols
            return buildAnnotatedString {
                refVerses?.get(verse)?.let { bibleRefVerse ->
                    bibleRefVerse.forEach {
                        append(it.name.toSpanStyle(verseTitleStyle))
                        append(DASH_SYMBOL)
                        append(it.text.toSpanStyle(normalContentStyle))
                        if (bibleRefVerse.last() != it) {
                            appendLine().appendLine()
                        }
                    }
                }
            }
        }

        private fun getContentStyleBy(
            contentType: ContentType, isDarkMode: Boolean
        ): SpanStyle {

            val normalContentStyle = SpanStyle(color = getNamedColor("Text", isDarkMode), fontSize = 22.sp)
            val noteContentStyle =
                SpanStyle(color = getNamedColor("Link", isDarkMode), fontSize = 18.sp, baselineShift = BaselineShift.Superscript)
            val jesusContentStyle = SpanStyle(color = Color.Red, fontSize = 18.sp)
            val referenceContentStyle =
                SpanStyle(color = getNamedColor("markupChoc", isDarkMode), fontSize = 14.sp, baselineShift = BaselineShift.Superscript)

            return when (contentType) {
                ContentType.JESUS -> jesusContentStyle
                ContentType.NORMAL -> normalContentStyle
                ContentType.NOTE -> noteContentStyle
                ContentType.REFERENCE -> referenceContentStyle
            }
        }

        private const val DOT_SYMBOL = "."
        private const val SPACE_SYMBOL = " "
        private const val DASH_SYMBOL = " - "
        private const val ONE_STAR_SYMBOL = "*"
        private const val TWO_STARS_SYMBOL = "**"
        private const val ONE_CROSS_SYMBOL = "†"
        private const val TWO_CROSSES_SYMBOL = "††"
        private const val ONE_STAR_ONE_CROSS_SYMBOL = "*†"
        private const val REV_REF_SYMBOL = "\u2192"
        private const val NEW_LINE_SYMBOL = "\n"
    }
}

data class ODRef(
    @SerializedName("refId") val refID: String,
    @SerializedName("articleId") val articleID: String,
    val title: String,
    val book: String,
    val author: String,
    val link: String,
    val posStart: Int,
    val posEnd: Int,
    val text: String,
    val verses: List<List<ODRefVerse>>,
    @SerializedName("author-photo-url-sm") val authorPhotoURLSm: String? = null,
    @SerializedName("author-photo-url-lg") val authorPhotoURLLg: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ODRef
        return refID == other.refID
    }

    override fun hashCode(): Int {
        return refID.hashCode()
    }
}

data class ODRefVerse(
    val type: String, val style: List<String>, val text: String, val ref: String? = null
)

data class BibleHits(
    val total: BibleTotal, @SerializedName("max_score") val maxScore: Int? = null, val hits: List<BibleHit>
)

data class BibleHit(
    @SerializedName("_index") val index: String,
    @SerializedName("_id") val id: String,
    @SerializedName("_score") val score: Int? = null,
    @SerializedName("_source") val source: Source,
    val sort: List<Int>
)

data class Source(
    val title: String? = null,
    val book: String,
    val chapter: Int,
    @SerializedName("chapter_link") val chapterLink: String,
    val verses: List<Verse>,
    val body: String,
    @SerializedName("_insert_idx") val insertIdx: Int,
    @SerializedName("_insert_ts") val insertTs: String
)

data class Verse(
    val name: String,
    val type: VerseType,
    val number: Int,
    val content: List<BibleVerseContent>,
    val references: References,
    @SerializedName("od-refs") val odRefs: List<String>,
    @SerializedName("reverse-references") val reverseReferences: List<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Verse
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

data class BibleVerseContent(
    val type: ContentType, val text: String
)

enum class ContentType {
    @SerializedName("Jesus")
    JESUS,

    @SerializedName("normal")
    NORMAL,

    @SerializedName("note")
    NOTE,

    @SerializedName("reference")
    REFERENCE
}

data class References(
    @SerializedName("*") val oneStar: List<String>? = null,
    @SerializedName("**") val twoStars: List<String>? = null,
    @SerializedName("†") val oneCross: List<String>? = null,
    @SerializedName("††") val twoCrosses: List<String>? = null,
    @SerializedName("*†") val starAndCross: List<String>? = null
)

enum class VerseType {
    @SerializedName("verse")
    VERSE
}

data class BibleTotal(
    val value: Int, val relation: String
)

data class BibleRef(
    val name: String, val link: String, val chapter: Int, val verses: List<BibleRefVerse>
)

data class BibleRefVerse(
    val type: String, val name: String, val number: Int, val text: String
)

