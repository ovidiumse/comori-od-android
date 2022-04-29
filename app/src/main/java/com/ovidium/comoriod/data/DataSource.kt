package com.ovidium.comoriod.data

import android.util.Log
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class DataSource {
    private val errorMessage = "Something went wrong"

    private fun <T> buildFlowImpl(
        externalScope: CoroutineScope,
        getter: suspend () -> T, state: MutableSharedFlow<Resource<T>>
    ) {
        externalScope.launch {
            state.emit(Resource.loading(null))
            try {
                val response = getter()
                state.emit(Resource.success(response))
            } catch (exception: Exception) {
                Log.e("DataSource", exception.message ?: errorMessage)
                state.emit(Resource.error(null, errorMessage))
            }
        }
    }

    fun <T> buildSharedFlow(
        externalScope: CoroutineScope,
        getter: suspend () -> T
    ): SharedFlow<Resource<T>> {
        val state = MutableSharedFlow<Resource<T>>()
        buildFlowImpl(externalScope, getter, state)
        return state
    }

    fun <T> buildFlow(
        externalScope: CoroutineScope,
        getter: suspend () -> T
    ): StateFlow<Resource<T>> {
        val state = MutableStateFlow<Resource<T>>(Resource.loading(null))
        buildFlowImpl(externalScope, getter, state)
        return state
    }
}