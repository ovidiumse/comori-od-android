package com.ovidium.comoriod.data.article

import androidx.compose.ui.text.AnnotatedString

class Article(
    val id: String,
    val title: AnnotatedString,
    val author: String,
    val volume: String,
    val book: String,
    val type: String,
    val body: AnnotatedString,
    val bibleRefs: Map<String, BibleRef>,
    val read_time: Int
) {
}