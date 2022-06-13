package com.ovidium.comoriod.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.SearchDataSource
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
import com.ovidium.comoriod.data.search.*
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchModel : ViewModel() {
    private val dataSource = SearchDataSource(RetrofitBuilder.apiService, viewModelScope)

    var isSearch = mutableStateOf(false)
    var autocompleteData = mutableStateOf<Resource<AutocompleteResponse>>(Resource.loading(null))

    class AggregationBucket(val key: String, val doc_count: Int)

    class Aggregation(
        var name: String = "",
        var paramName: String = "",
        var buckets: List<AggregationBucket> = emptyList()
    )

    class SearchData {
        var totalHitsCnt = mutableStateOf(0)
        var searchResults = mutableStateListOf<Hit>()
    }

    var searchData = mutableStateOf<Resource<SearchData>>(Resource.loading(null))
    var aggregations = mutableStateMapOf<Int, Aggregation>()

    fun autocomplete(query: String) {
        viewModelScope.launch {
            dataSource.autocomplete(query)
                .collectLatest { state -> autocompleteData.value = state }
        }
    }

    fun search(
        query: String,
        limit: Int = 20,
        offset: Int = 0,
        params: Map<String, String> = emptyMap()
    ) {
        fun handleResponse(response: Resource<SearchResponse>) {
            when (response.status) {
                Status.SUCCESS -> {
                    if (offset == 0) {
                        aggregations.clear()
                        searchData.value = Resource.success(SearchData())
                        response.data?.hits?.total?.value?.let { hitCnt ->
                            searchData.value.data?.totalHitsCnt?.value = hitCnt
                        }
                    }

                    response.data?.hits?.hits?.forEach { hit ->
                        searchData.value.data?.searchResults?.add(hit)
                    }
                }
                Status.LOADING -> searchData.value = Resource.loading(null)
                Status.ERROR -> searchData.value = Resource.error(null, response.message)
            }
        }

        viewModelScope.launch {
            dataSource.search(query, limit, offset, params)
                .collectLatest { response -> handleResponse(response) }
        }
    }

    private fun addAggregation(
        idx: Int,
        name: String,
        paramName: String,
        buckets: List<AggregationBucket>?
    ) {
        buckets?.let { bkts ->
            aggregations[idx] = Aggregation(name, paramName, bkts)
            val aggs = aggregations.map { key -> key }.joinToString(",")
            Log.d("SearchFilters", "Got ${name}, now have ${aggs}")
        }
    }

    fun getTypes(q: String = "", params: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            dataSource.getTypes(q, params).collectLatest { response ->
                if (response.status == Status.SUCCESS) {
                    addAggregation(
                        0,
                        "Tip",
                        "types",
                        response.data?.aggregations?.types?.buckets?.map { b ->
                            AggregationBucket(
                                b.key,
                                b.doc_count
                            )
                        })
                }
            }
        }
    }

    fun getAuthors(q: String = "", params: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            dataSource.getAuthors(q, params).collectLatest { response ->
                if (response.status == Status.SUCCESS) {
                    addAggregation(
                        1,
                        "Autori",
                        "authors",
                        response.data?.aggregations?.authors?.buckets?.map { b ->
                            AggregationBucket(
                                b.key,
                                b.doc_count
                            )
                        })
                }
            }
        }
    }

    fun getVolumes(q: String = "", params: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            dataSource.getVolumes(q, params).collectLatest { response ->
                if (response.status == Status.SUCCESS) {
                    addAggregation(
                        2,
                        "Volume",
                        "volumes",
                        response.data?.aggregations?.volumes?.buckets?.map { b ->
                            AggregationBucket(
                                b.key,
                                b.doc_count
                            )
                        })
                }
            }
        }
    }

    fun getBooks(q: String = "", params: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            dataSource.getBooks(q, params).collectLatest { response ->
                if (response.status == Status.SUCCESS) {
                    addAggregation(
                        3,
                        "Cărți",
                        "books",
                        response.data?.aggregations?.books?.buckets?.map { b ->
                            AggregationBucket(
                                b.key,
                                b.doc_count
                            )
                        })
                }
            }
        }
    }
}