package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ovidium.comoriod.data.volumes.Bucket
import com.ovidium.comoriod.data.volumes.VolumesResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient

@Composable
fun VolumesRow(
    navController: NavController,
    response: VolumesResponse?,
    isLoading: Boolean,
    isDark: Boolean
) {
    fun getAuthor(bucket: Bucket): String? {
        val authors = bucket.authors.buckets
        return if (authors.isEmpty()) null else authors[0].key
    }

    val items = response?.aggregations?.volumes?.buckets?.map { bucket ->
        val author = getAuthor(bucket)
        val imageId = if (author != null) getDrawableByAuthor(author) else null
        DataItem(
            bucket.key,
            "",
            author,
            imageId = imageId,
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