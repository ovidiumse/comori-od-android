package com.ovidium.comoriod.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.FavoritesDataSource
import com.ovidium.comoriod.utils.JWTUtils
import kotlinx.coroutines.launch

class FavoritesModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel): ViewModel() {
    private val dataSource = FavoritesDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    val favoriteArticlesData by lazy { dataSource.favoriteArticlesData }

    fun deleteFavoriteArticle(id: String) {
        viewModelScope.launch {
            dataSource.deleteFavoriteArticle(id)
        }
    }

}


class FavoritesModelFactory(
    private val jwtUtils: JWTUtils,
    private val signInModel: GoogleSignInModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(JWTUtils::class.java, GoogleSignInModel::class.java)
            .newInstance(jwtUtils, signInModel)
    }

}