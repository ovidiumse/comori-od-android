package com.ovidium.comoriod.views.library.books

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.pager.rememberPagerState
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.*
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.article.ArticleView
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BookScreen(
    book: String,
    jwtUtils: JWTUtils,
    scaffoldState: ScaffoldState,
    signInModel: GoogleSignInModel,
    favoritesModel: FavoritesModel,
    searchModel: SearchModel,
    markupsModel: MarkupsModel,
    readArticlesModel: ReadArticlesModel,
    navController: NavHostController
) {
    val isDark = isSystemInDarkTheme()
    val libraryModel: LibraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel))
    val bookModel: BookModel = viewModel()
    val articleModel: ArticleModel = viewModel()
    val titlesData by libraryModel.titlesData
    val titles = titlesData.data?.titles
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    var showTOCPopup by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            AppBar(
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState.drawerState) },
                onTitleClicked = {
                     navController.navigate(Screens.Library.route) {
                         launchSingleTop = true
                     }
                },
                actions = @Composable {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Cuprins",
                        modifier = Modifier.clickable(onClick = { showTOCPopup = true }),
                        tint = getNamedColor("Text", isDark = isDark)
                    )
                }
            )
        }
    ) {
        when(titlesData.status) {
            Status.SUCCESS -> {
                if (!titles.isNullOrEmpty()) {
                    HorizontalPager(
                        pageCount = titles.count(),
                        state = pagerState,
                        contentPadding = PaddingValues(end = 16.dp),
                        verticalAlignment = Alignment.Top,
                    ) { pageIdx ->
                        val markupIndexState = remember { mutableStateOf(0) }
                        val markupLengthState = remember { mutableStateOf(0) }

                        ArticleView(
                            articleID = titles.map { it._id }[pageIdx],
                            receivedMarkupIndex = markupIndexState,
                            receivedMarkupLength = markupLengthState,
                            signInModel = signInModel,
                            favoritesModel = favoritesModel,
                            searchModel = searchModel,
                            markupsModel = markupsModel,
                            readArticlesModel = readArticlesModel
                        )
                    }
                }
            }
            Status.ERROR -> {}
            Status.LOADING -> Text(text = "Loading...")
            Status.UNINITIALIZED -> {}
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

        if (titles.isNullOrEmpty()) {
            Text(text = "Loading...")
        } else {
            HorizontalPager(
                pageCount = titles.count(),
                state = pagerState,
                contentPadding = PaddingValues(end = 16.dp),
                verticalAlignment = Alignment.Top,
            ) { pageIdx ->
                val markupIndexState = remember { mutableStateOf(0) }
                val markupLengthState = remember { mutableStateOf(0) }

                ArticleView(
                    articleID = titles.map { it._id }[pageIdx],
                    receivedMarkupIndex = markupIndexState,
                    receivedMarkupLength = markupLengthState,
                    signInModel = signInModel,
                    favoritesModel = favoritesModel,
                    searchModel = searchModel,
                    markupsModel = markupsModel,
                    readArticlesModel = readArticlesModel
                )
            }
        }

    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            articleModel.clearBibleRefs()
        }
    }

    LaunchedEffect(Unit) {
        libraryModel.getTitles(book)
        bookModel.clear()
    }
}