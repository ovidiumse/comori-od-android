package com.ovidium.comoriod.data.search

data class Shards(
    val failed: Int,
    val skipped: Int,
    val successful: Int,
    val total: Int
)