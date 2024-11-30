package com.ovidium.comoriod.data.bible

data class BibleVerse(
    val formatedVerse: String,
    val formatedReference: List<String>,
    val odReferences: List<String>
)