package com.ovidium.comoriod.data.trending

data class TrendingResponseItem(
    val author: String,
    val book: String,
    val id: String,
    val reach: Int,
    val score: Double,
    val title: String,
    val views: Int,
    val volume: String
)