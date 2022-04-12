package com.ovidium.comoriod.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.SearchDataSource
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.utils.Resource

class SearchModel: ViewModel() {
    private val dataSource = SearchDataSource(RetrofitBuilder.apiService)

    fun search(q: String, limit: Int = 20, offset: Int = 0): LiveData<Resource<SearchResponse>> {
        return dataSource.search(q, limit, offset).asLiveData()
    }
}