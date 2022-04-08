package com.ovidium.comoriod.data.authors

data class AuthorsResponse(
    val _shards: Shards,
    val aggregations: Aggregations,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int
)