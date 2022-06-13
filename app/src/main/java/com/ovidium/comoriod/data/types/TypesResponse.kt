package com.ovidium.comoriod.data.types

data class TypesResponse(
    val _shards: Shards,
    val aggregations: Aggregations,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int
)