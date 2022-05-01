package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.data.recommended.RecommendedResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem
import com.ovidium.comoriod.views.ItemCategory

@Composable
fun RecommendedRow(navController: NavController, response: RecommendedResponse?, isLoading: Boolean, isDark: Boolean, showAuthorAction: (Bucket?) -> Unit) {
    val items = response?.map { item ->
        DataItem(
            item.title,
            item.author,
            item.book,
            getDrawableByAuthor(item.author),
            getVolumeCoverGradient(item.volume, isDark),
            type = ItemCategory.Article
        )
    }

    val show = isLoading || (items != null && items.isNotEmpty())
    if (show)
        HorizontalCarousel(navController, "Recomandate", items, 5, isLoading, showAuthorAction)
}