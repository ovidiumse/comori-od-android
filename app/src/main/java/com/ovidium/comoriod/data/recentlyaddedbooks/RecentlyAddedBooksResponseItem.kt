package com.ovidium.comoriod.data.recentlyaddedbooks

data class RecentlyAddedBooksResponseItem(
    val authors: List<Author>,
    val date_added: String,
    val doc_count: Int,
    val name: String,
    val volume: String
)