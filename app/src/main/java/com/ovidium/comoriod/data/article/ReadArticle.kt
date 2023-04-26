package com.ovidium.comoriod.data.article

data class ReadArticle(
    val author: String,
    val book: String,
    var count: Int,
    val id: String,
    val title: String,
    val volume: String,
    var timestamp: String?
)