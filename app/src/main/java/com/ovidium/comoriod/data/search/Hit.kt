package com.ovidium.comoriod.data.search

data class Hit(
    val _id: String,
    val _index: String,
    val _score: Double,
    val _source: Source,
    val highlight: Highlight,
    val sort: List<Any>
)