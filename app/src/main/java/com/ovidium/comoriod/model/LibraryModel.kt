package com.ovidium.comoriod.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.LibraryDataSource
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.concatenate
import kotlinx.coroutines.flow.collectLatest

const val TAG = "AggregationsModel"

class LibraryModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel) :
    ViewModel() {
    private val dataSource =
        LibraryDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    val authorsData by lazy { dataSource.authorsData }
    val volumesData by lazy { dataSource.volumesData }
    val booksData by lazy { dataSource.booksData }
    val recentlyAddedBooksData by lazy { dataSource.recentlyAddedBooksData }
    val recommendedData by lazy { dataSource.recommendedData }
    val trendingData by lazy { dataSource.trendingData }
    var titlesData = mutableStateOf<Resource<TitlesResponse>>(Resource.loading(null))
    var titlesForAuthorData = mutableStateOf<Resource<TitlesResponse>>(Resource.loading(null))

    suspend fun getTitles(bookTitle: String) {
        dataSource.getTitles(bookTitle).collectLatest { state ->
            titlesData.value = state
        }
    }

    suspend fun getTitlesForAuthor(
        authors: String = "",
        types: String = "",
        volumes: String = "",
        books: String = "",
        limit: Int = 20,
        offset: Int = 0,
    ) {
        dataSource.getTitlesForAuthor(
            authors = authors,
            types = types,
            volumes = volumes,
            books = books,
            offset = offset
        ).collectLatest { state ->
            println("TITLES: ${state.data}")
            titlesForAuthorData.value = state
        }
    }

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