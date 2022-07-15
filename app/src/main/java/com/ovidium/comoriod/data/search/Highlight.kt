package com.ovidium.comoriod.data.search

import com.google.gson.annotations.SerializedName

data class Highlight(
    val body: List<String>?,
    val title: List<String>?,
    @SerializedName("verses.text")
    val verses_text: List<String>?
)