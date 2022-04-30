package com.ovidium.comoriod.data.titles

data class Hit(
    val _id: String,
    val _index: String,
    val _score: Any?,
    val _source: Source,
    val sort: List<Int>
)