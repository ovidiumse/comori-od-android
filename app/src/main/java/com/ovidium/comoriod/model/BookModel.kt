package com.ovidium.comoriod.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.SearchDataSource
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.flow.collectLatest

class BookModel: ViewModel() {
    private val dataSource = SearchDataSource(RetrofitBuilder.apiService, viewModelScope)

    var bookData = mutableStateMapOf<String, Resource<ArticleResponse>>();

    suspend fun getArticle(id: String) {
        dataSource.getArticle(id).collectLatest { state -> bookData[id] = state }
    }
}