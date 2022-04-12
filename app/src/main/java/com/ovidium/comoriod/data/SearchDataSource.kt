package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.flow.Flow

class SearchDataSource(private val apiService: ApiService) : DataSource() {
    fun search(q: String, limit: Int = 20, offset: Int = 0): Flow<Resource<SearchResponse>> {
        return buildFlow { apiService.search(q, limit, offset) }
    }
}