package com.ovidium.comoriod.data.volumes

data class Bucket(
    val authors: Authors,
    val doc_count: Int,
    val key: String,
    val min_insert_ts: MinInsertTs
)