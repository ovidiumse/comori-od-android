package com.ovidium.comoriod.data.authors

data class Authors(
    val buckets: List<Bucket>,
    val doc_count_error_upper_bound: Int,
    val sum_other_doc_count: Int
)