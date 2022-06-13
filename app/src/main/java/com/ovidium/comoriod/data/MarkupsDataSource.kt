package com.ovidium.comoriod.data

import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody

class MarkupsDataSource(
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


    fun getMarkups(): SharedFlow<Resource<List<Markup>?>> {
        return buildSharedFlow(externalScope) {
            buildToken()?.let { token ->
                apiService.getMarkups(token)
            }
        }
    }

    fun deleteMarkup(id: String): StateFlow<Resource<ResponseBody?>> {
        return buildFlow(externalScope) {
            buildToken()?.let { token -> apiService.deleteMarkup(token, id) }
        }
    }

    fun saveMarkup(markup: Markup): StateFlow<Resource<Markup?>> {
        return buildFlow(externalScope) {
            buildToken()?.let { token ->
                apiService.saveMarkup(token, markup)
            }
        }
    }

}