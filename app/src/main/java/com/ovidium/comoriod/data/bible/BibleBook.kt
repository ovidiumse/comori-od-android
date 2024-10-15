package com.ovidium.comoriod.data.bible

import com.google.gson.annotations.SerializedName


data class BibleBooks(
    @SerializedName("Vechiul Testament")
    val oldTestamentBooks: List<BibleBook>,
    @SerializedName("Noul Testament")
    val newTestamentBooks: List<BibleBook>
)

data class BibleBook(
    val name: String,
    val shortName: String,
    val chapters: Int
)