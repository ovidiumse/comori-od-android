package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.books.BooksResponse
import com.ovidium.comoriod.data.books.Bucket
import com.ovidium.comoriod.utils.articulate
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

    fun getAuthorDisplay(bucket: Bucket): String {
        if (bucket.authors.buckets.size != 1)
            return articulate(bucket.authors.buckets.size, "autori", "autor")

        return bucket.authors.buckets[0].key
    }

    fun getAuthorPhotoUrl(bucket: Bucket): String? {
        if (bucket.authors.buckets.size != 1)
            return RetrofitBuilder.BASE_URL + "img/ic_unknown_person_sm.jpg"

        return bucket.authors.buckets[0].photo_url_sm
    }
    val items =
        response?.aggregations?.books?.buckets?.filter { bucket ->
            return@filter filter(bucket)
        }?.map { bucket ->
            val volume = getVolume(bucket)
            DataItem(
                title = bucket.key,
                id = bucket.key,
                secondary = getAuthorDisplay(bucket),
                detail = volume,
                image_url = getAuthorPhotoUrl(bucket),
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