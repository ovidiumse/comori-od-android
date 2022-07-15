package com.ovidium.comoriod.data.search

data class SearchResponse(
    val _shards: Shards,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int
)