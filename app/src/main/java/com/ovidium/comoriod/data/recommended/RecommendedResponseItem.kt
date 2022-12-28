package com.ovidium.comoriod.data.recommended

import com.google.gson.annotations.SerializedName

data class RecommendedResponseItem(
    val _id: String,
    val _insert_idx: Int,
    val _insert_ts: String,
    val _score: Double,
    val author: String,
    @SerializedName("author-photo-url-lg")
    val author_photo_url_lg: String?,
    @SerializedName("author-photo-url-sm")
    val author_photo_url_sm: String?,
    val book: String,
    val date_added: String,
    val full_book: String,
    val subtitle: List<Any>,
    val title: String,
    val type: String,
    val volume: String
)