package com.ovidium.comoriod.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.LibraryDataSource
import com.ovidium.comoriod.utils.JWTUtils

const val TAG = "AggregationsModel"

class LibraryModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel) :
    ViewModel() {
    private val dataSource = LibraryDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    val authorsData by lazy { dataSource.authorsData }
    val volumesData by lazy { dataSource.volumesData }
    val booksData by lazy { dataSource.booksData }
    val recentlyAddedBooksData by lazy { dataSource.recentlyAddedBooksData }
    val recommendedData by lazy { dataSource.recommendedData }
    val trendingData by lazy { dataSource.trendingData }
}

class LibraryModelFactory(
    private val jwtUtils: JWTUtils,
    private val signInModel: GoogleSignInModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(JWTUtils::class.java, GoogleSignInModel::class.java)
            .newInstance(jwtUtils, signInModel)
    }

}