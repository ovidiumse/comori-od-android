package com.ovidium.comoriod.utils

import com.ovidium.comoriod.data.search.Hits
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.data.search.Total
import com.ovidium.comoriod.data.titles.TitlesResponse

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String?): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = null)
    }
}

fun concatenateSearch(data: SearchResponse?, newData: SearchResponse?): Resource<SearchResponse> {
    val existingHits =
        data?.hits?.hits.let { it } ?: return Resource(Status.ERROR, null, "Error")
    val newHits = newData?.hits?.hits.let { it } ?: return Resource(Status.ERROR, null, "Error")
    val total =
        data?.hits?.total?.value.let { it } ?: return Resource(Status.ERROR, null, "Error")
    val aggs =
        data?.aggregations.let { it } ?: return Resource(Status.ERROR, null, "Error")
    val concatenatedHits =
        Hits((existingHits + newHits), max_score = 0, total = Total("", total))
    val response = SearchResponse(
        newData!!._shards,
        hits = concatenatedHits,
        aggregations = aggs,
        timed_out = false,
        took = 0
    )
    return Resource(Status.SUCCESS, response, message = null)
}

fun concatenateTitles(data: TitlesResponse, newData: TitlesResponse): Resource<TitlesResponse> {
    val existingHits = data.hits.hits
    val newHits = newData.hits.hits
    val total = data.hits.total.value
    val aggs = data.aggregations
    val concatenatedHits = com.ovidium.comoriod.data.titles.Hits((existingHits + newHits), max_score = 0, total = com.ovidium.comoriod.data.titles.Total("", total))
    val response = TitlesResponse(
        newData._shards,
        hits = concatenatedHits,
        timed_out = false,
        took = 0,
        aggregations = aggs
    )
    return Resource(Status.SUCCESS, response, message = null)
}