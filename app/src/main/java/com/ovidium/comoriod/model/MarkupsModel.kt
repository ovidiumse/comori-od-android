package com.ovidium.comoriod.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.MarkupsDataSource
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MarkupsModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel) : ViewModel() {
    private val dataSource =
        MarkupsDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    val markups = mutableStateOf<Resource<SnapshotStateList<Markup>>>(Resource.uninitialized())

    fun deleteMarkup(id: String) {
        viewModelScope.launch {
            dataSource.deleteMarkup(id).collectLatest { response ->
                if (response.status == Status.SUCCESS)
                    markups.value.data?.removeIf { markup -> markup.id == id }
            }
        }
    }

    fun save(markup: Markup) {
        viewModelScope.launch {
            dataSource.saveMarkup(markup).collectLatest { response ->
                if (response.status == Status.SUCCESS && response.data != null)
                    markups.value.data?.add(response.data)
            }
        }
    }

    fun loadMarkups() {
        Log.d("MarkupsModel", "Loading markups...")

        viewModelScope.launch {
            dataSource.getMarkups().collectLatest { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        markups.value = Resource.success(mutableStateListOf())
                        response.data?.forEach { markup ->
                            markups.value.data?.add(markup)
                        }
                    }
                    Status.LOADING -> markups.value = Resource.loading(null)
                    Status.ERROR -> markups.value = Resource.error(null, response.message)
                    Status.UNINITIALIZED -> markups.value = Resource.uninitialized()
                }
            }
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