package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ovidium.comoriod.data.volumes.Bucket
import com.ovidium.comoriod.data.volumes.VolumesResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem
import com.ovidium.comoriod.views.ItemCategory

@Composable
fun VolumesGrid(
    navController: NavController,
    response: VolumesResponse?,
    isLoading: Boolean,
    isDark: Boolean,
    authorFilter: String = ""
) {
    fun getAuthor(bucket: Bucket): String? {
        val authors = bucket.authors.buckets
        return if (authors.isEmpty()) null else authors[0].key
    }

    val items = response?.aggregations?.volumes?.buckets?.filter{ bucket ->
        (authorFilter.isEmpty() || getAuthor(bucket) == authorFilter)
    }?.map { bucket ->
        val author = getAuthor(bucket)
        DataItem(
            title = bucket.key,
            secondary = author,
            imageId = author?.let { getDrawableByAuthor(author) },
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