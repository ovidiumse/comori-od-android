package com.ovidium.comoriod.data.autocomplete

data class Hits(
    val hits: List<Hit>,
    val max_score: Double,
    val total: Total
)