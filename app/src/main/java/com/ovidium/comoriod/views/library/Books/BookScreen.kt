package com.ovidium.comoriod.views.library.Books

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.rememberPagerState
import com.ovidium.comoriod.model.BookModel
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.article.ArticleView

@Composable
fun BookScreen(book: String, jwtUtils: JWTUtils, signInModel: GoogleSignInModel) {

    val libraryModel: LibraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel))
    val bookModel: BookModel = viewModel()
    val titlesData by remember { libraryModel.titlesData }
    val pagerState = rememberPagerState()
    val titles = titlesData.data?.hits?.hits?.map { it._id }

    if (titles.isNullOrEmpty()) {
        Text(text = "Loading...")
    } else {
        HorizontalPager(
            count = titles.count(),
            contentPadding = PaddingValues(end = 16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ArticleView(articleID = titles[it])
        }
    }

    LaunchedEffect(Unit) {
        libraryModel.getTitles(book)
        bookModel.clear()
    }
}