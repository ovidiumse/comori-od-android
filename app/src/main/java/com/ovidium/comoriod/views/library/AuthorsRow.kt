package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem

@Composable
fun AuthorsRow(response: AuthorsResponse?, isLoading: Boolean, isDark: Boolean) {
    val items = response?.aggregations?.authors?.buckets?.map { bucket ->
        DataItem(
            bucket.key,
            "",
            "",
            getDrawableByAuthor(bucket.key),
            getVolumeCoverGradient("", isDark)
        )
    }

    AuthorsCarousel("Autori", items, 2, isLoading)
}