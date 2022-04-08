package com.ovidium.comoriod.data.authors

data class Bucket(
    val books: Books,
    val doc_count: Int,
    val key: String,
    val min_insert_ts: MinInsertTs,
    val types: Types,
    val volumes: Volumes
)