package com.ovidium.comoriod.data.titles

data class Hits(
    val hits: List<TitleHit>,
    val max_score: Any?,
    val total: Total
)