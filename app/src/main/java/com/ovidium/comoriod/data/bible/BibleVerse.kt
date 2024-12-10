package com.ovidium.comoriod.data.bible

import androidx.compose.ui.text.AnnotatedString

data class BibleVerse(
    val verseTitle: String,
    val formatedVerse: AnnotatedString,
    val formatedReference: AnnotatedString,
    val odReferences: List<String>
)