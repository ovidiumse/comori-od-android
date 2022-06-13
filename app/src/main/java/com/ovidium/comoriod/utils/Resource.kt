package com.ovidium.comoriod.utils

import com.ovidium.comoriod.data.search.Hits
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.data.search.Total
import com.ovidium.comoriod.data.titles.TitlesResponse

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String?): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = null)
    }
}
