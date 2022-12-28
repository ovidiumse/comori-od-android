package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.trending.TrendingResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient

@Composable
fun TrendingRow(
    navController: NavController,
    response: TrendingResponse?,
    isLoading: Boolean,
    isDark: Boolean
) {
    val items = response?.map { item ->
        DataItem(
            item.title,
            item.id,
            item.author,
            item.book,
            item.author_photo_url_sm ?: (RetrofitBuilder.BASE_URL + "img/ic_unknown_person_sm.jpg"),
            getVolumeCoverGradient(item.volume, isDark),
            type = ItemCategory.Article
        )
    }

    val show = isLoading || (items != null && items.isNotEmpty())
    if (show)
        HorizontalCarousel(navController, "Populare acum", items, 5, isLoading)
}