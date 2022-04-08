package com.ovidium.comoriod.data.recentlyaddedbooks

data class RecentlyAddedBooksResponseItem(
    val authors: List<String>,
    val date_added: String,
    val doc_count: Int,
    val name: String,
    val volume: String
)