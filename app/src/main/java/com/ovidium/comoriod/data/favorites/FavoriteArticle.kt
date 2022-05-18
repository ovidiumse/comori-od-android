package com.ovidium.comoriod.data.favorites

data class FavoriteArticle (
    val id: String,
    val title: String,
    val tags: List<String>,
    val author: String,
    val book: String,
    val timestamp: String,
        )