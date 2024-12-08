package com.ovidium.comoriod.data.bible

import androidx.compose.ui.text.AnnotatedString

data class BibleVerse(
    val formatedVerse: AnnotatedString,
    val formatedReference: AnnotatedString,
//    val reverseReferences: AnnotatedString,
    val odReferences: List<String>
)