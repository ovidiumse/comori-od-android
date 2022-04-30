package com.ovidium.comoriod.data.titles

data class TitlesResponse(
    val _shards: Shards,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int
)