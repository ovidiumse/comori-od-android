package com.ovidium.comoriod.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.MarkupsDataSource
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MarkupsModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel) : ViewModel() {
    private val dataSource =
        MarkupsDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    val markups =
        mutableStateOf<Resource<List<Markup>?>>(Resource.loading(null))

    init {
        viewModelScope.launch {
            updateMarkups()
        }
    }

    fun deleteMarkup(id: String) {
        viewModelScope.launch {
            dataSource.deleteMarkup(id)
            updateMarkups()
        }
    }

    fun save(markup: Markup) {
        viewModelScope.launch {
            dataSource.saveMarkup(markup)
            updateMarkups()
        }
    }

    private suspend fun updateMarkups() {
        dataSource.getMarkups().collectLatest { state ->
            markups.value = state
        }
    }
}


class MarkupsModelFactory(
    private val jwtUtils: JWTUtils,
    private val signInModel: GoogleSignInModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(JWTUtils::class.java, GoogleSignInModel::class.java)
            .newInstance(jwtUtils, signInModel)
    }

}