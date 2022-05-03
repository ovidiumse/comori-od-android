package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

class LibraryDataSource(
    private val jwtUtils: JWTUtils,
    private val apiService: ApiService,
    private val signInModel: GoogleSignInModel,
    private val externalScope: CoroutineScope
) : DataSource() {
    private fun buildToken(): String? {
        val userData = signInModel.userResource.value
        if (userData.user?.id == null)
            return null

        return when (userData.state) {
            UserState.LoggedIn ->
                jwtUtils.buildToken(
                    userData.user.id,
                    userData.user.issuer
                )

            else -> null
        }
    }

    val authorsData by lazy { buildFlow(externalScope) { apiService.getAuthors() } }
    val volumesData by lazy { buildFlow(externalScope) { apiService.getVolumes() } }
    val booksData by lazy { buildFlow(externalScope) { apiService.getBooks() } }

    val recentlyAddedBooksData by lazy { buildFlow(externalScope) { apiService.getRecentlyAddedBooks() } }

    val recommendedData by lazy {
        buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.getRecommended(token) }
        }
    }

    val trendingData by lazy {
        buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.getTrending(token) }
        }
    }

    fun getTitles(bookTitle: String): Flow<Resource<TitlesResponse>> {
        return buildFlow(externalScope) { apiService.getTitles(bookTitle) }
    }

    fun getTitlesForAuthor(authors: String, types: String, volumes: String, books: String, offset: Int, limit: Int): Flow<Resource<TitlesResponse>> {
        return buildFlow(externalScope) { apiService.getTitlesForAuthor(authors = authors, type = types, volumes = volumes, books = books, offset = offset, limit = limit) }
    }

}