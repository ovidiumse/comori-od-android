package com.ovidium.comoriod.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.MainActivity
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.LibraryDataSource
import com.ovidium.comoriod.data.titles.TitleHit
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.Dispatchers
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

    class TitlesData {
        var totalHitsCnt = mutableStateOf(0)
        var titles = mutableStateListOf<TitleHit>()
    }

    var titlesData = mutableStateOf<Resource<TitlesData>>(Resource.loading(null))

    private fun handleResponse(offset: Int, response: Resource<TitlesResponse>) {
        when (response.status) {
            Status.SUCCESS -> {
                if (offset == 0) {
                    titlesData.value = Resource.success(TitlesData())
                    response.data?.hits?.total?.value?.let { hitCnt ->
                        titlesData.value.data?.totalHitsCnt?.value = hitCnt
                    }
                }

                response.data?.hits?.hits?.forEach { hit ->
                    titlesData.value.data?.titles?.add(hit)
                }
            }
            Status.LOADING -> titlesData.value = Resource.loading(null)
            Status.ERROR -> titlesData.value = Resource.error(null, response.message)
        }
    }

    fun getTitles(bookTitle: String) {
        viewModelScope.launch {
            dataSource.getTitles(limit = 10000, params = mapOf("books" to bookTitle))
                .collectLatest { response ->
                    handleResponse(0, response)
                }
        }
    }

    fun getTitles(
        limit: Int = 20, offset: Int = 0, params: Map<String, String> = emptyMap()
    ) {
        viewModelScope.launch {
            dataSource.getTitles(offset = offset, limit = limit, params = params)
                .collectLatest { response ->
                    handleResponse(offset, response)
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