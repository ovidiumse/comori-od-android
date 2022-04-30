package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import com.ovidium.comoriod.data.volumes.Bucket
import com.ovidium.comoriod.data.volumes.VolumesResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem

@Composable
fun VolumesGrid(response: VolumesResponse?, isLoading: Boolean, isDark: Boolean) {
    fun getAuthor(bucket: Bucket): String? {
        val authors = bucket.authors.buckets
        return if (authors.isEmpty()) null else authors[0].key
    }

    val items = response?.aggregations?.volumes?.buckets?.map { bucket ->
        val author = getAuthor(bucket)
        DataItem(
            title = bucket.key,
            secondary = author,
            imageId = author?.let { getDrawableByAuthor(author) },
            gradient = getVolumeCoverGradient(bucket.key, isDark)
        )
    }

    ItemsGrid(names = Pair("volum", "volume"), items, estimatedSize = 12, isLoading = isLoading)
}