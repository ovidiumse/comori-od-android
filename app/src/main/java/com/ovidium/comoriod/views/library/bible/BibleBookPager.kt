package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.getFormatedText
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.library.StateHandler

@Composable
fun BibleBookPager(
    bibleBook: String,
    scaffoldState: ScaffoldState,
    libraryModel: LibraryModel,
    navController: NavHostController
) {

    val pagerState = rememberPagerState()
    val bibleChapterData by libraryModel.bibleChapterData.collectAsState()

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
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    when (state?.status) {
                        Status.SUCCESS -> {
                            // TODO beautify this text
                            val formatedText = state.data?.getFormatedText()

                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                formatedText?.let {
                                    itemsIndexed(formatedText) { index, text ->
                                        Text(
                                            text = text.formatedVerse + "\n" + "\n"
                                                    + text.formatedReference.toString()
                                        )
                                    }
                                }
                            }
                        }

                        // TODO add a loading spinner for LOADING state
                        Status.ERROR,
                        Status.LOADING,
                        Status.UNINITIALIZED,
                        null -> {}
                    }
                }

                LaunchedEffect(Unit) {
                    libraryModel.getBibleChapter(currentBibleBook.name, pageIdx + 1)
                }
            }
        }
    }
}

