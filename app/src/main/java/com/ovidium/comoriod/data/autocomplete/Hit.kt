package com.ovidium.comoriod.data.autocomplete

data class Hit(
    val _id: String,
    val _index: String,
    val _score: Double,
    val _source: Source
)