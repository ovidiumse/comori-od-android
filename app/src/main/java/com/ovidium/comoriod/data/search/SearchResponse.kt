package com.ovidium.comoriod.data.search

import com.google.gson.annotations.SerializedName
import com.ovidium.comoriod.data.titles.Shards

data class SearchResponse(
    val _shards: Shards,
    val hits: Hits,
    val timed_out: Boolean,
    val took: Int,
    val aggregations: Aggregations
)

data class Aggregations (
    val types: AggregationCategory,
    val books: AggregationCategory,
    val volumes: AggregationCategory,
    val authors: AggregationCategory
)

data class AggregationCategory (
    val docCountErrorUpperBound: Long,
    val sumOtherDocCount: Long,
    val buckets: List<Bucket>
)

data class Bucket (
    val key: String,
    @SerializedName("doc_count")
    val docCount: Int,
    val minInsertTs: MinInsertTs
)

data class MinInsertTs (
    val value: Long,
    val valueAsString: String
)