package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedText
import com.ovidium.comoriod.data.bible.ODRef
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.Montserrat
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
    val scrollState = rememberScrollState()
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
                Text(
                    modifier = Modifier
                        .padding(bottom = 12.dp),
                    text = "${currentAuthor.value?.name} - ${pagerState.currentPage + 1}/${filteredODRefs.size}"
                )
                HorizontalDivider(
                    modifier = Modifier
                        .alpha(0.5f)
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(1f),
                    thickness = 0.5.dp
                )
                HorizontalPager(
                    pagerState
                ) { pageIDX ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            Text(
                                modifier = Modifier
                                    .padding(bottom = 16.dp),
                                text = filteredODRefs[pageIDX].title,
                                fontSize = 22.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = filteredODRefs[pageIDX].text,
                            fontFamily = Montserrat,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}