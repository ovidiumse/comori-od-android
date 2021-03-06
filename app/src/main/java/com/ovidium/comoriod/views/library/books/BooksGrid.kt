package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.ovidium.comoriod.data.books.BooksResponse
import com.ovidium.comoriod.data.books.Bucket
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient

@Composable
fun BooksGrid(
    navController: NavController,
    response: BooksResponse?,
    isLoading: Boolean,
    isDark: Boolean,
    filter: (bucket: Bucket) -> Boolean
) {
    fun getVolume(bucket: Bucket): String {
        val volumes = bucket.volumes.buckets
        return if (volumes.isEmpty()) "" else volumes[0].key
    }

    fun getGradient(bucket: Bucket): List<Color> {
        return getVolumeCoverGradient(getVolume(bucket), isDark)
    }

    fun getAuthor(bucket: Bucket): String? {
        val authors = bucket.authors.buckets
        return if (authors.isEmpty()) null else authors[0].key
    }

    val items =
        response?.aggregations?.books?.buckets?.filter { bucket ->
            return@filter filter(bucket)
        }?.map { bucket ->
            val author = getAuthor(bucket)
            val volume = getVolume(bucket)
            DataItem(
                title = bucket.key,
                id = bucket.key,
                secondary = author,
                detail = volume,
                imageId = author?.let { getDrawableByAuthor(author) },
                gradient = getGradient(bucket),
                type = ItemCategory.Book
            )
        }

    ItemsGrid(
        navController,
        names = Pair("carte", "cărți"),
        items,
        estimatedSize = 50,
        isLoading = isLoading
    )
}