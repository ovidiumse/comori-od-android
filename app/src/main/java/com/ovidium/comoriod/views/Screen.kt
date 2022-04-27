package com.ovidium.comoriod.views

import androidx.annotation.DrawableRes
import com.ovidium.comoriod.R
import java.net.URLEncoder

enum class Screens(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int,
    val isMenu: Boolean = false
) {
    Library(route = "library", title = "Library", icon = R.drawable.ic_outline_menu_book_24, true),
    Search(route = "search", title = "Search", icon = R.drawable.ic_baseline_search_24, true),
    Favourites(
        route = "favourites",
        title = "Favourites",
        icon = R.drawable.ic_baseline_star_border_24,
        true
    ),
    Article(
        route = "article",
        title = "Article",
        icon = R.drawable.ic_baseline_search_24
    ),

    Filter(
        route = "filter",
        title = "Filter",
        icon = R.drawable.ic_filter
    );

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                val encodedArg = URLEncoder.encode(arg, "utf-8")
                append("/$encodedArg")
            }
        }
    }
}