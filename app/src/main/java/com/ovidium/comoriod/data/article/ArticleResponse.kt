package com.ovidium.comoriod.data.article

import com.google.gson.annotations.SerializedName

data class ArticleResponse(
    val _id: String,
    val _index: String,
    val _insert_idx: Int,
    val _insert_ts: String,
    val author: String,
    @SerializedName("bible-refs")
    val bibleRefs: Map<String, BibleRef>,
    val book: String,
    val date_added: String,
    val full_book: String,
    val subtitle: List<Any>,
    val title: String,
    val type: String,
    val verses: List<List<ArticleResponseChunk>>,
    val volume: String
)