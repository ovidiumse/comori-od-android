package com.ovidium.comoriod.data.article

import com.google.gson.annotations.SerializedName

data class SearchArticleResponse(
    val _id: String,
    val _index: String,
    val _primary_term: Int,
    val _seq_no: Int,
    val _source: Source,
    val _type: String,
    val _version: Int,
    val found: Boolean
)

data class Source(
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