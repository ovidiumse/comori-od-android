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
    val scrollOffset: Int = 0,
    val bgColor: String = "",
    val timestamp: String = "",
    var tags: List<String> = emptyList()
)