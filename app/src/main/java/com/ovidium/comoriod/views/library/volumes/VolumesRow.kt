package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ovidium.comoriod.api.RetrofitBuilder
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

    fun getAuthorImageUrl(bucket: Bucket): String? {
        if (bucket.authors.buckets.size != 1)
            return RetrofitBuilder.BASE_URL + "img/ic_unknown_person_sm.jpg"

        return bucket.authors.buckets[0].photo_url_sm
    }
    val items = response?.aggregations?.volumes?.buckets?.map { bucket ->
        DataItem(
            bucket.key,
            "",
            getAuthorDisplay(bucket),
            image_url = getAuthorImageUrl(bucket),
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