package com.ovidium.comoriod.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.SearchDataSource
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.flow.collectLatest

class ArticleModel: ViewModel() {
    private val dataSource = SearchDataSource(RetrofitBuilder.apiService, viewModelScope)

    var articleData = mutableStateOf<Resource<ArticleResponse>>(Resource.loading(null))


    suspend fun getArticle(id: String) {
        dataSource.getArticle(id).collectLatest { state -> articleData.value = state }
    }
}