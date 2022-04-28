package com.ovidium.comoriod.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.SearchDataSource
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.concatenate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

class SearchModel : ViewModel() {
    private val dataSource = SearchDataSource(RetrofitBuilder.apiService, viewModelScope)


    var query = mutableStateOf("")
    var isSearch = mutableStateOf(false)
    var autocompleteData = mutableStateOf<Resource<AutocompleteResponse>>(Resource.loading(null))
    var searchData = mutableStateOf<Resource<SearchResponse>>(Resource.loading(null))

    suspend fun autocomplete() {
        dataSource.autocomplete(query.value)
            .collectLatest { state -> autocompleteData.value = state }
    }

    suspend fun search(limit: Int = 20, offset: Int = 0, params: String) {
        dataSource.search(query.value, "", limit, offset, params).collectLatest { state ->
            println("RECEIVED SEARCH: ${state.data?.hits?.total?.value} - PARAMS: ${params.replace(" ", "%20")}")
            if (offset == 0) {
                searchData.value = state
            } else {
                searchData.value = concatenate(searchData.value.data, state.data)
            }
        }
    }

}