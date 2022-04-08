package com.ovidium.comoriod.data.books

data class BooksResponse(
    val _shards: Shards,
    val aggregations: Aggregations,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int
)