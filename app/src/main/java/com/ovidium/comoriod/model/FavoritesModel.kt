package com.ovidium.comoriod.model

import android.util.Log
import androidx.annotation.Keep
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.FavoritesDataSource
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Keep
class FavoritesModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel) : ViewModel() {
    private val dataSource =
        FavoritesDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    val favorites = mutableStateOf<Resource<SnapshotStateList<FavoriteArticle>>>(Resource.uninitialized())

    fun isFavorite(id: String): Boolean {
        return favorites.value.data?.map { fav -> fav.id }?.contains(id) ?: false
    }

    fun deleteFavoriteArticle(id: String) {
        viewModelScope.launch {
            dataSource.deleteFavoriteArticle(id).collectLatest { response ->
                if (response.status == Status.SUCCESS)
                    favorites.value.data?.removeIf { favorite -> favorite.id == id }
            }
        }
    }

    fun saveFavorite(article: FavoriteArticle) {
        viewModelScope.launch {
            dataSource.saveFavorite(article).collectLatest { response ->
                if (response.status == Status.SUCCESS && response.data != null)
                    favorites.value.data?.add(response.data)
            }
        }
    }

    fun loadFavorites() {
        Log.d("FavoritesModel", "Loading favorites...")

        viewModelScope.launch {
            dataSource.getFavoriteArticles().collectLatest { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        favorites.value = Resource.success(mutableStateListOf())
                        response.data?.forEach { favorite ->
                            favorites.value.data?.add(favorite)
                        }
                    }
                    Status.LOADING -> favorites.value = Resource.loading(null)
                    Status.ERROR -> favorites.value = Resource.error(null, response.message)
                    Status.UNINITIALIZED -> favorites.value = Resource.uninitialized()
                }
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