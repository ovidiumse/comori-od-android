package com.ovidium.comoriod.data.authors

data class Books(
    val buckets: List<BucketX>,
    val doc_count_error_upper_bound: Int,
    val sum_other_doc_count: Int
)