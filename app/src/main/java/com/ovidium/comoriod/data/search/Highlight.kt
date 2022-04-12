package com.ovidium.comoriod.data.search

import com.google.gson.annotations.SerializedName

data class Highlight(
    val title: List<String>,
    @SerializedName("verses.text")
    val versesText: List<String>
)