package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ovidium.comoriod.data.books.BooksResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem

@Composable
fun BooksGrid(response: BooksResponse?, isLoading: Boolean, isDark: Boolean) {
    fun getVolume(bucket: com.ovidium.comoriod.data.books.Bucket): String {
        val volumes = bucket.volumes.buckets
        return if (volumes.isEmpty()) "" else volumes[0].key
    }

    fun getGradient(bucket: com.ovidium.comoriod.data.books.Bucket): List<Color> {
        return getVolumeCoverGradient(getVolume(bucket), isDark)
    }

    fun getAuthor(bucket: com.ovidium.comoriod.data.books.Bucket): String? {
        val authors = bucket.authors.buckets
        return if (authors.isEmpty()) null else authors[0].key
    }

    val items = response?.aggregations?.books?.buckets?.map { bucket ->
        val author = getAuthor(bucket)
        val volume = getVolume(bucket)
        DataItem(
            title = bucket.key,
            secondary = author,
            detail = volume,
            imageId = author?.let { getDrawableByAuthor(author) },
            gradient = getGradient(bucket)
        )
    }

    ItemsGrid(names = Pair("carte", "cărți"), items, estimatedSize = 50, isLoading = isLoading)
}