package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.library.StateHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleBookPager(
    bibleBook: String,
    libraryModel: LibraryModel,
    navController: NavHostController
) {

    val bibleChapterData by libraryModel.bibleChapterData.collectAsState()
    val verseFullReferenceData by libraryModel.verseFullReferenceData.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet = remember { mutableStateOf(false) }

    StateHandler(navController, libraryModel.bibleBooksData) { data, _ ->
        data?.let {
            val currentBibleBook = (data.newTestamentBooks + data.oldTestamentBooks).first { it.name == bibleBook }
            val pagerState = rememberPagerState(
                initialPage = 0,
                initialPageOffsetFraction = 0f,
                pageCount = { currentBibleBook.chapters }
            )
            HorizontalPager(
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
                    Text("${currentBibleBook.name} ${pageIdx + 1}")
                    when (state?.status) {
                        Status.SUCCESS -> {
                            BibleBookContainer(
                                state.data,
                                scope,
                                showBottomSheet,
                                libraryModel,
                                navController
                            )
                            if (showBottomSheet.value) {
                                ModalBottomSheet(
                                    onDismissRequest = {
                                        scope.launch {
                                            showBottomSheet.value = false
                                        }
                                    },
                                    sheetState = sheetState
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentSize()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = verseFullReferenceData,
                                            color = Color.Gray,
                                            modifier = Modifier
                                                .padding(bottom = 2.dp)
                                        )
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

                    LaunchedEffect(Unit) {
                        libraryModel.getBibleChapter(currentBibleBook.name, pageIdx + 1)
                    }
                }

                LaunchedEffect(Unit) {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion { showBottomSheet.value = false }
                }
            }
        }
    }
}

