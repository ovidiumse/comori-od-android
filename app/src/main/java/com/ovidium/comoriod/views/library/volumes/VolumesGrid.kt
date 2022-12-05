package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ovidium.comoriod.data.volumes.Bucket
import com.ovidium.comoriod.data.volumes.VolumesResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.articulate
import com.ovidium.comoriod.utils.getVolumeCoverGradient

@Composable
fun VolumesGrid(
    navController: NavController,
    response: VolumesResponse?,
    isLoading: Boolean,
    isDark: Boolean,
    author: String = ""
) {
    fun hasAuthor(bucket: Bucket, author: String): Boolean {
        return bucket.authors.buckets.any { a -> a.key == author }
    }

    fun getAuthorDisplay(bucket: Bucket): String {
        if (bucket.authors.buckets.size != 1)
            return articulate(bucket.authors.buckets.size, "autori", "autor")

        return bucket.authors.buckets[0].key
    }

    fun getAuthorImageId(bucket: Bucket): Int {
        if (bucket.authors.buckets.size != 1)
            return getDrawableByAuthor("Unknown")

        return getDrawableByAuthor(bucket.authors.buckets[0].key)
    }

    val items = response?.aggregations?.volumes?.buckets?.filter{ bucket ->
        (author.isEmpty() || hasAuthor(bucket, author))
    }?.map { bucket ->
        DataItem(
            title = bucket.key,
            id = "",
            secondary = getAuthorDisplay(bucket),
            imageId = getAuthorImageId(bucket),
            gradient = getVolumeCoverGradient(bucket.key, isDark),
            type = ItemCategory.Volume
        )
    }

    ItemsGrid(
        navController,
        names = Pair("volum", "volume"),
        items,
        estimatedSize = 12,
        isLoading = isLoading
    )
}