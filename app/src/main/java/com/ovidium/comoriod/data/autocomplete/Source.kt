package com.ovidium.comoriod.data.autocomplete

import com.google.gson.annotations.SerializedName

data class Source(
    val _insert_idx: Int,
    val _insert_ts: String,
    val author: String,

    @SerializedName("bible-ref")
    val bibleRef: String,
    val book: String,
    val date_added: String,
    val full_book: String,
    val subtitle: List<Any>,
    val title: String,
    val type: String,
    val volume: String
)