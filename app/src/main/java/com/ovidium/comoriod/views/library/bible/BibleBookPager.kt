package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedText
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.library.StateHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BibleBookPager(
    bibleBook: String,
    scaffoldState: ScaffoldState,
    libraryModel: LibraryModel,
    navController: NavHostController
) {

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val bibleChapterData by libraryModel.bibleChapterData.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()

    StateHandler(navController, libraryModel.bibleBooksData) { data, isLoading ->
        data?.let {
            val currentBibleBook = (data.newTestamentBooks + data.oldTestamentBooks).first { it.name == bibleBook }
            HorizontalPager(
                pageCount = currentBibleBook.chapters,
                state = pagerState,
                verticalAlignment = Alignment.Top,
            ) { pageIdx ->

                val state = bibleChapterData[Pair(currentBibleBook.name, pageIdx + 1)]

                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    when (state?.status) {
                        Status.SUCCESS -> {

                            BottomSheetScaffold(
                                scaffoldState = bottomSheetScaffoldState,
                                sheetShape = RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 4.dp
                                ),
                                sheetPeekHeight = 0.dp,
                                sheetContent = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentSize()
                                            .padding(16.dp)
                                    ) {
                                        CommonBottomSheetHeader()
                                        //ceva dinamic
                                    }
                                }
                            ) {

                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    state.data?.getFormatedText(isDarkTheme)?.let {
                                        itemsIndexed(it) { _, bibleVerseContent ->
                                            BibleVerseItem(
                                                bibleVerseContent,
                                                scope,
                                                bottomSheetScaffoldState,
                                                scaffoldState, libraryModel, navController
                                            )
                                        }
                                    }
                                }
                            }

                            LaunchedEffect(Unit) {
                                scope.launch {
                                    if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                }
                            }
                        }

                        // TODO add a loading spinner for LOADING state
                        Status.ERROR,
                        Status.LOADING,
                        Status.UNINITIALIZED,
                        null -> {
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    libraryModel.getBibleChapter(currentBibleBook.name, pageIdx + 1)
                }
            }
        }
    }
}

