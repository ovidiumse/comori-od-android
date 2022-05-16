package com.ovidium.comoriod.data.titles

data class Source(
    val _insert_idx: Int,
    val _insert_ts: String,
    val author: String,
    val book: String,
    val date_added: String,
    val full_book: String,
    val subtitle: List<String>,
    val title: String,
    val type: String,
    val volume: String
)