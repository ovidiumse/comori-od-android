package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ovidium.comoriod.data.recentlyaddedbooks.RecentlyAddedBooksResponse
import com.ovidium.comoriod.data.recentlyaddedbooks.RecentlyAddedBooksResponseItem
import com.ovidium.comoriod.data.volumes.Bucket
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.articulate
import com.ovidium.comoriod.utils.getVolumeCoverGradient

@Composable
fun RecentlyAddedBooksRow(
    navController: NavController,
    response: RecentlyAddedBooksResponse?,
    isLoading: Boolean,
    isDark: Boolean
) {
    fun getAuthorDisplay(item: RecentlyAddedBooksResponseItem): String {
        if (item.authors.size != 1)
            return articulate(item.authors.size, "autori", "autor")

        return item.authors[0]
    }

    fun getAuthorImageId(item: RecentlyAddedBooksResponseItem): Int {
        if (item.authors.size != 1)
            return getDrawableByAuthor("Unknown")

        return getDrawableByAuthor(item.authors[0])
    }

    val items = response?.map { item ->
        DataItem(
            item.name,
            "",
            getAuthorDisplay(item),
            item.volume,
            getAuthorImageId(item),
            getVolumeCoverGradient(item.volume, isDark),
            type = ItemCategory.Book
        )
    }

    val show = isLoading || (items != null && items.isNotEmpty())
    if (show)
        HorizontalCarousel(navController, "Adăugate recent", items, 2, isLoading)
}