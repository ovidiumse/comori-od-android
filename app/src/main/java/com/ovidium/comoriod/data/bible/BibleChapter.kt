package com.ovidium.comoriod.data.bible
import com.google.gson.annotations.SerializedName

data class BibleChapter(
    val took: Int,
    @SerializedName("timed_out")
    val timedOut: Boolean,
    val hits: BibleHits,
    @SerializedName("bible-refs")
    val bibleRefs: Map<String, BibleRef>?,
    @SerializedName("od-refs")
    val odRefs: Map<String, ODRef>?
)

data class ODRef(
    @SerializedName("refId")
    val refID: String,
    @SerializedName("articleId")
    val articleID: String,
    val title: String,
    val book: String,
    val author: String,
    val link: String,
    val posStart: Int,
    val posEnd: Int,
    val text: String,
    val verses: List<List<ODRefVerse>>,
    @SerializedName("author-photo-url-sm")
    val authorPhotoURLSm: String? = null,
    @SerializedName("author-photo-url-lg")
    val authorPhotoURLLg: String? = null
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
    val type: String,
    val style: List<String>,
    val text: String,
    val ref: String? = null
)

data class BibleHits(
    val total: BibleTotal,
    @SerializedName("max_score")
    val maxScore: Int? = null,
    val hits: List<BibleHit>
)

data class BibleHit(
    @SerializedName("_index")
    val index: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("_score")
    val score: Int? = null,
    @SerializedName("_source")
    val source: Source,
    val sort: List<Int>
)

data class Source(
    val title: String? = null,
    val book: String,
    val chapter: Int,
    @SerializedName("chapter_link")
    val chapterLink: String,
    val verses: List<Verse>,
    val body: String,
    @SerializedName("_insert_idx")
    val insertIdx: Int,
    @SerializedName("_insert_ts")
    val insertTs: String
)

data class Verse(
    val name: String,
    val type: VerseType,
    val number: Int,
    val content: List<BibleVerseContent>,
    val references: References,
    @SerializedName("od-refs")
    val odRefs: List<String>,
    @SerializedName("reverse-references")
    val reverseReferences: List<String>? = null
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
    val type: ContentType,
    val text: String
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
    @SerializedName("*")
    val oneStar: List<String>? = null,
    @SerializedName("**")
    val twoStars: List<String>? = null,
    @SerializedName("†")
    val oneCross: List<String>? = null,
    @SerializedName("††")
    val twoCrosses: List<String>? = null,
    @SerializedName("*†")
    val starAndCross: List<String>? = null
)

enum class VerseType {
    @SerializedName("verse")
    VERSE
}

data class BibleTotal(
    val value: Int,
    val relation: String
)

data class BibleRef(
    val name: String,
    val link: String,
    val chapter: Int,
    val verses: List<BibleRefVerse>
)

data class BibleRefVerse(
    val type: String,
    val name: String,
    val number: Int,
    val text: String
)

