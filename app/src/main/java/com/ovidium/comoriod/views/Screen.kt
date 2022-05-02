package com.ovidium.comoriod.views

import androidx.annotation.DrawableRes
import com.ovidium.comoriod.R

enum class Screens(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int,
    val isMenu: Boolean = false
) {
    Library(route = "library", title = "Library", icon = R.drawable.ic_outline_menu_book_24, true),
    Search(route = "search", title = "Search", icon = R.drawable.ic_baseline_search_24, true),
    Book(route = "book", title = "Book", icon = R.drawable.ic_outline_menu_book_24),
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

    BooksForVolume(
        route = "booksForVolume",
        title = "Books for volume",
        icon = R.drawable.ic_book
    );

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}