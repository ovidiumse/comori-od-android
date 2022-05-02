package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.data.recentlyaddedbooks.RecentlyAddedBooksResponse
import com.ovidium.comoriod.data.recentlyaddedbooks.RecentlyAddedBooksResponseItem
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem
import com.ovidium.comoriod.views.ItemCategory

@Composable
fun RecentlyAddedBooksRow(
    navController: NavController,
    response: RecentlyAddedBooksResponse?,
    isLoading: Boolean,
    isDark: Boolean
) {
    fun getAuthor(item: RecentlyAddedBooksResponseItem): String? {
        return if (item.authors.isEmpty()) null else item.authors[0]
    }

    val items = response?.map { item ->
        val author = getAuthor(item)

        DataItem(
            item.name,
            author,
            item.volume,
            author?.let { getDrawableByAuthor(author) },
            getVolumeCoverGradient(item.volume, isDark),
            type = ItemCategory.Book
        )
    }

    val show = isLoading || (items != null && items.isNotEmpty())
    if (show)
        HorizontalCarousel(navController, "Adăugate recent", items, 2, isLoading)
}