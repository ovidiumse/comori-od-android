package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.model.ReadArticlesModel
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.library.StateHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleBookPager(
    bibleBook: String,
    libraryModel: LibraryModel,
    signInModel: GoogleSignInModel,
    favoritesModel: FavoritesModel,
    searchModel: SearchModel,
    markupsModel: MarkupsModel,
    readArticlesModel: ReadArticlesModel,
    navController: NavHostController
) {

    val bibleChapterData by libraryModel.bibleChapterData.collectAsState()
    val verseFullReferenceData by libraryModel.verseFullReferenceData.collectAsState()
    val odReferenceData by libraryModel.odReferenceData.collectAsState()

    val verseRefSheetState = rememberModalBottomSheetState()
    val showVerseRefBottomSheet = remember { mutableStateOf(false) }
    val odRefSheetState = rememberModalBottomSheetState()
    val chapterListSheetState = rememberModalBottomSheetState()
    val showOdRefBottomSheet = remember { mutableStateOf(false) }
    val showChapterListBottomSheet = remember { mutableStateOf(false) }
    val currentAuthor: MutableState<CountedAuthor?> = remember { mutableStateOf(null) }

    val scope = rememberCoroutineScope()

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
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state?.status) {
                        Status.SUCCESS -> {
                            AssistChip(
                                onClick = {
                                    scope.launch {
                                        showChapterListBottomSheet.value = true
                                    }
                                },
                                label = {
                                    Text(
                                        text = "${currentBibleBook.name} ${pageIdx + 1}",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Gray
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_down),
                                        tint = getNamedColor("Link", isSystemInDarkTheme()),
                                        contentDescription = "Chapter List",
                                    )
                                },
                            )
                            BibleBookContainer(
                                state.data,
                                scope,
                                showVerseRefBottomSheet,
                                showOdRefBottomSheet,
                                currentAuthor,
                                libraryModel
                            )
                            if (showVerseRefBottomSheet.value) {
                                ModalBottomSheet(
                                    onDismissRequest = {
                                        scope.launch {
                                            showVerseRefBottomSheet.value = false
                                        }
                                    },
                                    sheetState = verseRefSheetState
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
                            if (showChapterListBottomSheet.value) {
                                ModalBottomSheet(
                                    containerColor = Color.DarkGray,
                                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) },
                                    onDismissRequest = {
                                        scope.launch {
                                            showChapterListBottomSheet.value = false
                                        }
                                    },
                                    sheetState = chapterListSheetState
                                ) {
                                    ChapterListBottomView(libraryModel, currentBibleBook, scope, pagerState, showChapterListBottomSheet)
                                }
                            }
                            if (showOdRefBottomSheet.value) {
                                ModalBottomSheet(
                                    containerColor = getNamedColor("HeaderBar", isSystemInDarkTheme()),
                                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) },
                                    onDismissRequest = {
                                        scope.launch {
                                            showOdRefBottomSheet.value = false
                                        }
                                    },
                                    sheetState = odRefSheetState
                                ) {
                                    ODRefsBottomView(
                                        state,
                                        libraryModel,
                                        signInModel,
                                        favoritesModel,
                                        searchModel,
                                        markupsModel,
                                        readArticlesModel,
                                        currentAuthor,
                                        odReferenceData
                                    )
                                }
                            }

                        }

                        Status.ERROR -> {
                            Text(
                                text = "A apărut o eroare, te rugăm să revii mai târziu.",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }

                        Status.LOADING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.width(48.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }

                        Status.UNINITIALIZED -> {
                            Text(
                                text = "A apărut o eroare, te rugăm să revii mai târziu.",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }

                        null -> {
                        }
                    }

                    LaunchedEffect(Unit) {
                        libraryModel.getBibleChapter(currentBibleBook.name, pageIdx + 1)
                    }
                }

                LaunchedEffect(Unit) {
                    scope.launch {
                        verseRefSheetState.hide()
                    }.invokeOnCompletion { showVerseRefBottomSheet.value = false }
                }
            }
        }
    }
}




