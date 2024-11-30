package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.recommended.RecommendedResponse
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

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
    val bibleBooksData by lazy { buildFlow(externalScope) { apiService.getBibleBooks() } }

    val recentlyAddedBooksData by lazy { buildFlow(externalScope) { apiService.getRecentlyAddedBooks() } }

    val trendingData by lazy {
        buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.getTrending(token) }
        }
    }

    fun getTitles(
        limit: Int = 20,
        offset: Int = 0,
        params: Map<String, String> = emptyMap()
    ): Flow<Resource<TitlesResponse>> {
        return buildSharedFlow(externalScope) {
            apiService.getTitles(limit, offset, "|", params)
        }
    }

    fun getRecommended(): Flow<Resource<RecommendedResponse?>> {
        return buildSharedFlow(externalScope) {
            buildToken()?.let { token -> apiService.getRecommended(token) }
        }
    }

    fun getBibleChapterData(
        bibleBook: String,
        chapterNumber: Int
    ): Flow<Resource<BibleChapter>> =
        buildFlow(externalScope) { apiService.getBibleChapter(bibleBook, chapterNumber) }
}