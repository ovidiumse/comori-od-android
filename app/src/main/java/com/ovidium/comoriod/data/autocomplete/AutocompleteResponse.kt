package com.ovidium.comoriod.data.autocomplete

data class AutocompleteResponse(
    val _shards: Shards,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int
)