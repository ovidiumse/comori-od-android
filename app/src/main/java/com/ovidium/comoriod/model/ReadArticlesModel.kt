package com.ovidium.comoriod.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.ReadArticlesDataSource
import com.ovidium.comoriod.data.article.ReadArticle
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.JWTUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class ReadArticlesModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel, val libraryModel: LibraryModel) : ViewModel() {
    private val dataSource =
        ReadArticlesDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    val readArticles = mutableStateOf<Resource<SnapshotStateList<ReadArticle>>>(Resource.uninitialized())

    init {
        loadReadArticles()
    }

    fun addOrUpdate(readArticle: ReadArticle) {
        viewModelScope.launch {
            var addResponse: StateFlow<Resource<ResponseBody?>>? = null
            var existingArticle: ReadArticle? = null

            if (readArticles.value.status == Status.SUCCESS) {
                existingArticle = readArticles.value.data?.firstOrNull { a -> a.id == readArticle.id }
            }

            if (existingArticle != null) {
                existingArticle.count += readArticle.count
                existingArticle.timestamp = readArticle.timestamp
                Log.d("ReadArticles model", "Timestamp now utc: ${readArticle.timestamp}")
                dataSource.updateReadArticle(existingArticle)
            } else {
                Log.d("ReadArticles model", "Timestamp now utc: ${readArticle.timestamp}")
                addResponse = dataSource.addReadArticle(readArticle)
            }

            addResponse?.collectLatest { response ->
                when(response.status) {
                    Status.SUCCESS -> {
                        loadReadArticles()
                        libraryModel.loadRecommended()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadReadArticles() {
        viewModelScope.launch {
            Log.d("ReadArticles model", "Loading read articles...")
            dataSource.getReadArticles().collectLatest { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        Log.d("ReadArticles model", "Got ")
                        readArticles.value = Resource.success(mutableStateListOf())
                        response.data?.forEach { readArticle ->
                            readArticles.value.data?.add(readArticle)
                        }
                    }
                    Status.LOADING -> readArticles.value = Resource.loading(null)
                    Status.ERROR -> readArticles.value = Resource.error(null, response.message)
                    Status.UNINITIALIZED -> readArticles.value = Resource.uninitialized()
                }

            }
        }
    }
}

class ReadArticlesModelFactory(
    private val jwtUtils: JWTUtils,
    private val signInModel: GoogleSignInModel,
    private val libraryModel: LibraryModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(JWTUtils::class.java, GoogleSignInModel::class.java, LibraryModel::class.java)
            .newInstance(jwtUtils, signInModel, libraryModel)
    }
}