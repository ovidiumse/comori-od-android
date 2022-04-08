package com.ovidium.comoriod.data.books

data class Bucket(
    val authors: Authors,
    val doc_count: Int,
    val key: String,
    val min_insert_ts: MinInsertTs,
    val volumes: Volumes
)