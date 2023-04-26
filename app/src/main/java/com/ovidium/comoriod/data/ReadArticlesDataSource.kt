package com.ovidium.comoriod.data

import android.util.Log
import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.article.ReadArticle
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody

class ReadArticlesDataSource(
    private val jwtUtils: JWTUtils,
    private val apiService: ApiService,
    private val signInModel: GoogleSignInModel,
    private val externalScope: CoroutineScope
) : DataSource() {
    private fun buildToken(): String? {
        val userData = signInModel.userResource.value
        Log.d("ReadArticlesDataSource", "Building token for user id ${userData.user?.id}")
        if (userData.user?.id == null)
            return null

        return when (userData.state) {
            UserState.LoggedIn ->
                jwtUtils.buildToken(
                    userData.user.id,
                    userData.user.issuer
                )
            else -> {
                Log.d("ReadArticlesDataSource", "User not logged in!")
                null
            }
        }
    }

    fun getReadArticles(): SharedFlow<Resource<List<ReadArticle>?>> {
        return buildSharedFlow(externalScope) {
            buildToken()?.let { token ->
                apiService.getReadArticles(token)
            }
        }
    }

    fun updateReadArticle(readArticle: ReadArticle): StateFlow<Resource<ResponseBody?>> {
        return buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.updateReadArticle(token, readArticle.id, readArticle) }
        }
    }

    fun addReadArticle(readArticle: ReadArticle): StateFlow<Resource<ResponseBody?>> {
        Log.d("ReadArticlesDataSource", "Attempting to log read article ${readArticle.id}")

        return buildFlow(externalScope) {
            buildToken()?.let { token ->
                Log.d("ReadArticlesDataSource", "Logging read article with token $token")
                apiService.addReadArticle(token, readArticle)
            }
        }
    }
}