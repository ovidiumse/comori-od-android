package com.ovidium.comoriod.data.books

data class Authors(
    val buckets: List<BucketX>,
    val doc_count_error_upper_bound: Int,
    val sum_other_doc_count: Int
)