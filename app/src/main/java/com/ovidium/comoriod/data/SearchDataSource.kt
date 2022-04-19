package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

class SearchDataSource(
    private val apiService: ApiService,
    private val externalScope: CoroutineScope
) : DataSource() {
    fun autocomplete(prefix: String): SharedFlow<Resource<AutocompleteResponse>> {
        return buildSharedFlow(externalScope) { apiService.autocomplete(prefix) }
    }

    fun search(q: String, limit: Int = 20, offset: Int = 0): SharedFlow<Resource<SearchResponse>> {
        return buildSharedFlow(externalScope) { apiService.search(q, limit, offset) }
    }
}