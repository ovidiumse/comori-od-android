package com.ovidium.comoriod.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.DataSource
import com.ovidium.comoriod.data.LibraryDataSource
import com.ovidium.comoriod.utils.JWTUtils
import kotlin.time.ExperimentalTime

const val TAG = "AggregationsModel"

class LibraryModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel) :
    ViewModel() {
    private val dataSource = LibraryDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel)

    val authorsData by lazy { dataSource.authorsData.asLiveData() }
    val volumesData by lazy { dataSource.volumesData.asLiveData() }
    val booksData by lazy { dataSource.booksData.asLiveData() }
    val recentlyAddedBooksData by lazy { dataSource.recentlyAddedBooksData.asLiveData() }
    val recommendedData by lazy { dataSource.recommendedData.asLiveData() }
    val trendingData by lazy { dataSource.trendingData.asLiveData()}
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