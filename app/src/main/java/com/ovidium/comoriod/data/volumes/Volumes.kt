package com.ovidium.comoriod.data.volumes

data class Volumes(
    val buckets: List<Bucket>,
    val doc_count_error_upper_bound: Int,
    val sum_other_doc_count: Int
)