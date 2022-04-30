package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import com.ovidium.comoriod.data.trending.TrendingResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem

@Composable
fun TrendingRow(response: TrendingResponse?, isLoading: Boolean, isDark: Boolean) {
    val items = response?.map { item ->
        DataItem(
            item.title,
            item.author,
            item.book,
            getDrawableByAuthor(item.author),
            getVolumeCoverGradient(item.volume, isDark),
        )
    }

    val show = isLoading || (items != null && items.isNotEmpty())
    if (show)
        HorizontalCarousel("Populare acum", items, 5, isLoading)
}