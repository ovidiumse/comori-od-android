package com.ovidium.comoriod.data.types

data class Hits(
    val hits: List<Any>,
    val max_score: Any,
    val total: Total
)