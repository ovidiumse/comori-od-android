package com.ovidium.comoriod.data

import android.util.Log
import com.ovidium.comoriod.api.ApiService
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class DataSource {
    private val errorMessage = "Something went wrong"

    fun <T> buildFlow(getter: suspend () -> T): Flow<Resource<T>> {
        return flow {
            emit(Resource.loading(null))
            try {
                val response = getter()
                emit(Resource.success(response))
            } catch (exception: Exception) {
                Log.e("DataSource", exception.message ?: errorMessage)
                emit(Resource.error(null, errorMessage))
            }
        }
    }
}