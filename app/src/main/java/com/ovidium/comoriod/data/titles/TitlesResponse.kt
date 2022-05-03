package com.ovidium.comoriod.data.titles

import com.ovidium.comoriod.data.search.Aggregations

data class TitlesResponse(
    val _shards: Shards,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int,
    val aggregations: Aggregations
)