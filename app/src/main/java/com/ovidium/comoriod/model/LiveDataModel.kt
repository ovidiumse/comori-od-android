package com.ovidium.comoriod.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
open class LiveDataModel : ViewModel() {
    protected fun <T> liveDataWrapper(name: String, getter: suspend () -> T) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            val loadingStart = System.nanoTime()
            try {
                val data = getter()
                val elapsed = System.nanoTime() - loadingStart
                Log.d("LiveDataModel", "Loaded $name in ${elapsed.toDuration(DurationUnit.NANOSECONDS)}")
                emit(Resource.success(data))
            } catch (exception: Exception) {
                emit(
                    Resource.error(
                        data = null,
                        message = exception.message ?: "Loading $name failed!"
                    )
                )
            }
        }
}