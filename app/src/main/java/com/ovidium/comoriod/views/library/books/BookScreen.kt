package com.ovidium.comoriod.views.library.books

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ovidium.comoriod.components.BookTopBar
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.*
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.article.ArticleView
import kotlinx.coroutines.launch

@Composable
fun BookScreen(
    book: String,
    jwtUtils: JWTUtils,
    scaffoldState: ScaffoldState,
    signInModel: GoogleSignInModel
) {

    val libraryModel: LibraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel))
    val bookModel: BookModel = viewModel()
    val articleModel: ArticleModel = viewModel()
    val titlesData by remember { libraryModel.titlesData }
    val pagerState = rememberPagerState()
    val titles = titlesData.data?.hits?.hits?.map { it }
    val coroutineScope = rememberCoroutineScope()
    var showTOCPopup by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            BookTopBar(
                title = { Text(text = "Comori OD") },
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState) },
                onTOCClicked = { showTOCPopup = true }
            )
        }
    ) {
        if (titles.isNullOrEmpty()) {
            Text(text = "Loading...")
        } else {
            HorizontalPager(
                count = titles.count(),
                state = pagerState,
                contentPadding = PaddingValues(end = 16.dp),
                verticalAlignment = Alignment.Top,
            ) { pageIdx ->
                ArticleView(articleID = titles.map { it._id }[pageIdx])
            }
        }
        if (showTOCPopup && titles != null) {
            TOCPopup(
                titles = titles,
                currentIndex = pagerState.currentPage,
                focusRequester = focusRequester,
                onSelectAction = { selectedIndex ->
                    coroutineScope.launch {
                        pagerState.scrollToPage(selectedIndex)
                        showTOCPopup = false
                    }
                },
                onExitAction = { showTOCPopup = false },
            )
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect() { page ->
            articleModel.clearBibleRefs()
        }
    }

    LaunchedEffect(Unit) {
        libraryModel.getTitles(book)
        bookModel.clear()
    }
}