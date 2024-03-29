package com.ovidium.comoriod.views.library.books

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.MainActivity
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.library.StateHandler
import com.ovidium.comoriod.views.library.BooksGrid

@Composable
fun BooksForAuthorScreen(
    navController: NavController,
    author: String,
    jwtUtils: JWTUtils,
    signInModel: GoogleSignInModel
) {

    val libraryModel: LibraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel))

    Column {
        StateHandler(
            navController = navController,
            responseData = libraryModel.booksData
        ) { data, isLoading ->
            BooksGrid(
                navController = navController,
                response = data,
                isLoading = isLoading,
                isDark = isSystemInDarkTheme(),
                filter = { bucket ->
                    var hasAuthor = false
                    bucket.authors.buckets.forEach { a -> if (a.key == author) hasAuthor = true }
                    hasAuthor
                }
            )
        }
    }
}