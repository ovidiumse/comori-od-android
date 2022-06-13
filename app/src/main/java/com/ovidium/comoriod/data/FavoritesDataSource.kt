package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody

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

    fun getFavoriteArticles(): SharedFlow<Resource<List<FavoriteArticle>?>> {
        return buildSharedFlow(externalScope) {
            buildToken()?.let { token ->
                apiService.getFavorites(token)
            }
        }
    }

    fun deleteFavoriteArticle(id: String): StateFlow<Resource<ResponseBody?>> {
        return buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.deleteFavoriteArticle(token, id) }
        }
    }

    fun saveFavorite(article: FavoriteArticle): StateFlow<Resource<FavoriteArticle?>> {
        return buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.saveFavorite(token, article) }
        }
    }

}
