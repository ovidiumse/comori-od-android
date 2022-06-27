package com.ovidium.comoriod.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.SearchDataSource
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.article.SearchArticleResponse
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookModel: ViewModel() {
    private val dataSource = SearchDataSource(RetrofitBuilder.apiService, viewModelScope)

    var bookData = mutableStateMapOf<String, Resource<ArticleResponse>>()
    var searchData = mutableStateMapOf<String, Resource<SearchArticleResponse>>()

    fun clear() {
        bookData.clear()
    }

    fun getArticle(id: String, searchTerm: String, isSearch: String?) {
        viewModelScope.launch {
            if (searchTerm.isNotEmpty() && isSearch == "true") {
                dataSource.getSearchArticle(id, searchTerm).collectLatest { state -> searchData[id] = state}
            } else {
                dataSource.getArticle(id).collectLatest { state -> bookData[id] = state }
            }
        }
    }
}