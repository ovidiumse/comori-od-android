package com.ovidium.comoriod.data

import androidx.compose.runtime.collectAsState
import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class SearchDataSource(
    private val apiService: ApiService,
    private val externalScope: CoroutineScope
) : DataSource() {
    fun autocomplete(prefix: String): SharedFlow<Resource<AutocompleteResponse>> {
        return buildSharedFlow(externalScope) { apiService.autocomplete(prefix) }
    }

    fun search(q: String, includeAggs: String, limit: Int = 20, offset: Int = 0, type: String = "", authors: String = "", volumes: String = "", books: String = ""): SharedFlow<Resource<SearchResponse>> {
        return buildSharedFlow(externalScope) { apiService.search(q, includeAggs, type, authors, volumes, books, limit, offset) }
    }

    fun getArticle(id: String): SharedFlow<Resource<ArticleResponse>> {
        return buildSharedFlow(externalScope) { apiService.getArticle(id) }
    }
}