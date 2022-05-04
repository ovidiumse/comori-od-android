package com.ovidium.comoriod.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.LibraryDataSource
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.concatenateTitles
import com.ovidium.comoriod.views.search.filter.FilterCategory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
    val searchParams = mutableStateMapOf<FilterCategory, MutableList<String>>()

    fun getTitles(bookTitle: String) {
        viewModelScope.launch {
            dataSource.getTitles(bookTitle).collectLatest { state ->
                titlesData.value = state
            }
        }
    }

    fun getTitlesForAuthor(
        authors: String = "",
        types: String = "",
        volumes: String = "",
        books: String = "",
        limit: Int = 20,
        offset: Int = 0,
    ) {
        viewModelScope.launch {
            dataSource.getTitlesForAuthor(
                authors = authors,
                types = types,
                volumes = volumes,
                books = books,
                offset = offset,
                limit = limit
            ).collectLatest { state ->
                if (offset == 0) {
                    titlesForAuthorData.value = state
                } else {
                    if (titlesForAuthorData.value.data != null && state.data != null) {
                        concatenateTitles(titlesForAuthorData.value.data!!, state.data).also { titlesForAuthorData.value = it }
                    }
                }
            }
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