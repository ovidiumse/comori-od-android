package com.ovidium.comoriod.views

import androidx.annotation.DrawableRes
import com.ovidium.comoriod.R

sealed class Screen(val route: String, val title: String, @DrawableRes val icon: Int) {
    object Library: Screen(route="library", title="Library", icon= R.drawable.ic_outline_menu_book_24)
    object Search: Screen(route="search", title="Search", icon=R.drawable.ic_baseline_search_24)
    object Favourites: Screen(route="favourites", title="Favourites", icon=R.drawable.ic_baseline_star_border_24)
}


sealed class SearchScreens(val route: String, val title: String, @DrawableRes val icon: Int) {
    object SearchResults: SearchScreens("search_results", title = "Search Results", icon = R.drawable.ic_baseline_search_24)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}