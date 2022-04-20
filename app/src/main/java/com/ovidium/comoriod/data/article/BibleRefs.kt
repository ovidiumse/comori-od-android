package com.ovidium.comoriod.data.article

data class BibleRef(
    val book: String,
    val name: String,
    val link: String,
    val chapter: Long,
    val verses: List<BibleRefVerse>
)

data class BibleRefVerse (
    val type: PurpleType,
    val name: String,
    val number: Long,
    val text: String
)

enum class PurpleType {
    Verse
}

data class ArticleResponseVerse (
    val type: String,
    val style: List<String>,
    val text: String,
    val ref: String? = null
)

enum class VerseType {
    BibleRef,
    Normal
}