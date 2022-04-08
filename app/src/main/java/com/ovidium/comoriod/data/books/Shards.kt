package com.ovidium.comoriod.data.books

data class Shards(
    val failed: Int,
    val skipped: Int,
    val successful: Int,
    val total: Int
)