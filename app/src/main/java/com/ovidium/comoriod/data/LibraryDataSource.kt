package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils

class LibraryDataSource(private val jwtUtils: JWTUtils,
                        private val apiService: ApiService,
                        private val signInModel: GoogleSignInModel
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

    val authorsData by lazy { buildFlow { apiService.getAuthors() } }
    val volumesData by lazy { buildFlow { apiService.getVolumes() } }
    val booksData by lazy { buildFlow { apiService.getBooks() } }

    val recentlyAddedBooksData by lazy { buildFlow { apiService.getRecentlyAddedBooks() } }

    val recommendedData by lazy {
        buildFlow {
            buildToken()?.let { token -> apiService.getRecommended(token) }
        }
    }

    val trendingData by lazy {
        buildFlow {
            buildToken()?.let { token -> apiService.getTrending(token) }
        }
    }
}