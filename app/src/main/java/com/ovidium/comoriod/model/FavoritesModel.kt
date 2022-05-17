package com.ovidium.comoriod.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.FavoritesDataSource
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel): ViewModel() {
    private val dataSource = FavoritesDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    var favoriteArticlesData = mutableStateOf<Resource<List<FavoriteArticle>?>>(Resource.loading(null))


    init {
        updateFavorites()
    }

    fun deleteFavoriteArticle(id: String) {
        viewModelScope.launch {
            dataSource.deleteFavoriteArticle(id)
            updateFavorites()
        }
    }

    fun saveFavorite(article: FavoriteArticle) {
        viewModelScope.launch {
            dataSource.saveFavorite(article)
            updateFavorites()
        }
    }

    private fun updateFavorites() {
        viewModelScope.launch {
            dataSource.getFavoriteArticles().collectLatest { state ->
                favoriteArticlesData.value = state
            }
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