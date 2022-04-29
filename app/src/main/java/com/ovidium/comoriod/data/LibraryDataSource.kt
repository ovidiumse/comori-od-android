package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils
import kotlinx.coroutines.CoroutineScope

class LibraryDataSource(
    private val jwtUtils: JWTUtils,
    private val apiService: ApiService,
    private val signInModel: GoogleSignInModel,
    externalScope: CoroutineScope
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
}