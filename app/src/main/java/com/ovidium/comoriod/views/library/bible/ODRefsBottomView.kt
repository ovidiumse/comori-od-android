package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedText
import com.ovidium.comoriod.data.bible.ODRef
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ODRefsBottomView(
    state: Resource<BibleChapter?>,
    libraryModel: LibraryModel,
    currentAuthor: MutableState<CountedAuthor?>,
    odReferenceData: Map<String, ODRef>,
) {

    val filteredODRefs = odReferenceData.values.filter { it.author == currentAuthor.value?.name }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { filteredODRefs.size }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(16.dp)
            .fillMaxSize()
    ) {
        state.data?.let { bibleChapter ->
            libraryModel.currentVerseData.value?.let { currentVerse ->

                ODRefsBottomSheetAuthorCarousel(
                    libraryModel,
                    currentVerse,
                    bibleChapter,
                    currentAuthor,
                    isSystemInDarkTheme(),
                    { }
                )
                HorizontalPager(
                    pagerState
                ) { pageIDX ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                                text = "${currentAuthor.value?.name} - ${pageIDX + 1}/${filteredODRefs.size}"
                            )
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                                text = filteredODRefs[pageIDX].title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = filteredODRefs[pageIDX].text
                        )
                    }
                }
            }
        }
    }
}