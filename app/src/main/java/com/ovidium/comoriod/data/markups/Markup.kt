package com.ovidium.comoriod.data.markups

import com.google.gson.annotations.SerializedName

data class Markup(
    @SerializedName("_id")
    val id: String,
    val title: String = "",
    val book: String = "",
    val articleID: String = "",
    val selection: String = "",
    val index: Int,
    val length: Int,
    val bgColor: String = "",
    val timestamp: String = "",
    val tags: List<String> = emptyList()
)