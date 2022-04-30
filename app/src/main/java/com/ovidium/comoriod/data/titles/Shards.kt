package com.ovidium.comoriod.data.titles

data class Shards(
    val failed: Int,
    val skipped: Int,
    val successful: Int,
    val total: Int
)