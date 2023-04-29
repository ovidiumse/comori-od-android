package com.ovidium.comoriod.data

import androidx.compose.runtime.collectAsState
import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.article.SearchArticleResponse
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
import com.ovidium.comoriod.data.books.BooksResponse
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.data.types.TypesResponse
import com.ovidium.comoriod.data.volumes.VolumesResponse
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class SearchDataSource(
    private val apiService: ApiService, private val externalScope: CoroutineScope
) : DataSource() {
    fun autocomplete(prefix: String): SharedFlow<Resource<AutocompleteResponse>> {
        return buildSharedFlow(externalScope) { apiService.autocomplete(prefix) }
    }

    fun search(
        q: String, limit: Int = 20, offset: Int = 0, params: Map<String, String>
    ): SharedFlow<Resource<SearchResponse>> {
        return buildSharedFlow(externalScope) {
            apiService.search(
                q, limit, offset, "|", params
            )
        }
    }

    fun getSearchArticle(id: String, searchTerm: String): SharedFlow<Resource<SearchArticleResponse>> {
        return buildSharedFlow(externalScope) { apiService.getSearchArticle(id, searchTerm) }
    }

    fun getArticle(id: String): SharedFlow<Resource<ArticleResponse>> {
        return buildSharedFlow(externalScope) { apiService.getArticle(id) }
    }

    fun getTypes(
        q: String = "", params: Map<String, String> = emptyMap()
    ): SharedFlow<Resource<TypesResponse>> {
        return buildSharedFlow(externalScope) {
            apiService.getTypes(q, "|", params.filter { (key, _) -> key != "types" })
        }
    }

    fun getAuthors(
        q: String = "", params: Map<String, String> = emptyMap()
    ): SharedFlow<Resource<AuthorsResponse>> {
        return buildSharedFlow(externalScope) {
            apiService.getAuthors(q, "|", params.filter { (key, _) -> key != "authors" })
        }
    }

    fun getVolumes(
        q: String = "", params: Map<String, String> = emptyMap()
    ): SharedFlow<Resource<VolumesResponse>> {
        return buildSharedFlow(externalScope) {
            apiService.getVolumes(q, "|", params.filter { (key, _) -> key != "volumes" })
        }
    }

    fun getBooks(
        q: String = "", params: Map<String, String> = emptyMap()
    ): SharedFlow<Resource<BooksResponse>> {
        return buildSharedFlow(externalScope) {
            apiService.getBooks(q, "|", params.filter { (key, _) -> key != "books" })
        }
    }
}