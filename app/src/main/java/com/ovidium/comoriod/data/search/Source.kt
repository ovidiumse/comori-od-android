package com.ovidium.comoriod.data.search

import com.google.gson.annotations.SerializedName

data class Source(
    val _insert_idx: Int,
    val _insert_ts: String,
    val author: String,
    @SerializedName("bible-ref")
    val bible_ref: String,
    val book: String,
    val date_added: String,
    val full_book: String,
    val subtitle: List<Any>,
    val title: String,
    val type: String,
    val volume: String
)