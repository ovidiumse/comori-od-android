package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ovidium.comoriod.data.volumes.Bucket
import com.ovidium.comoriod.data.volumes.VolumesResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.articulate
import com.ovidium.comoriod.utils.getVolumeCoverGradient

@Composable
fun VolumesRow(
    navController: NavController,
    response: VolumesResponse?,
    isLoading: Boolean,
    isDark: Boolean
) {
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
    val items = response?.aggregations?.volumes?.buckets?.map { bucket ->
        DataItem(
            bucket.key,
            "",
            getAuthorDisplay(bucket),
            imageId = getAuthorImageId(bucket),
            gradient = getVolumeCoverGradient(bucket.key, isDark),
            type = ItemCategory.Volume
        )
    }

    HorizontalCarousel(
        navController,
        name = "Volume",
        items,
        estimatedSize = 7,
        isLoading,
        isLast = true
    )
}