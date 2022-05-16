package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class FavoritesDataSource(
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

    val favoriteArticlesData by lazy {
        buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.getFavorites(token) }
        }
    }

    fun deleteFavoriteArticle(id: String) {
        buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.deleteFavoriteArticle(token, id) }
        }
    }

}
