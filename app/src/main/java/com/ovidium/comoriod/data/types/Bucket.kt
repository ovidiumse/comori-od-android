package com.ovidium.comoriod.data.types

data class Bucket(
    val doc_count: Int,
    val key: String,
    val min_date_added: MinDateAdded,
    val min_insert_ts: MinInsertTs
)