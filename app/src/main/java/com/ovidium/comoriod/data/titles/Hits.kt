package com.ovidium.comoriod.data.titles

data class Hits(
    val hits: List<Hit>,
    val max_score: Any?,
    val total: Total
)