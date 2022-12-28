package com.ovidium.comoriod.data.trending

import com.google.gson.annotations.SerializedName

data class TrendingResponseItem(
    val author: String,
    @SerializedName("author-photo-url-sm")
    val author_photo_url_sm: String?,
    @SerializedName("author-photo-url-lg")
    val author_photo_url_lg: String?,
    val book: String,
    val id: String,
    val reach: Int,
    val score: Double,
    val title: String,
    val views: Int,
    val volume: String
)