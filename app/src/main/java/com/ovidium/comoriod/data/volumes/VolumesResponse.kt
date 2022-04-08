package com.ovidium.comoriod.data.volumes

data class VolumesResponse(
    val _shards: Shards,
    val aggregations: Aggregations,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int
)