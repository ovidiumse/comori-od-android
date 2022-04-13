package com.ovidium.comoriod.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.SearchDataSource
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.flow.SharedFlow

class SearchModel : ViewModel() {
    private val dataSource = SearchDataSource(RetrofitBuilder.apiService, viewModelScope)



    fun autocomplete(prefix: String): SharedFlow<Resource<AutocompleteResponse>> {
        return dataSource.autocomplete(prefix)
    }

    fun search(q: String, limit: Int = 20, offset: Int = 0): SharedFlow<Resource<SearchResponse>> {
        return dataSource.search(q, limit, offset)
    }
}