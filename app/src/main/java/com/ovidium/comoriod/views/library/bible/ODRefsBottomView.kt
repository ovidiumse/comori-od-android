package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedText
import com.ovidium.comoriod.data.bible.ODRef
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.GoogleSignInModelFactory
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.model.ReadArticlesModel
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.Montserrat
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.views.article.ArticleView
import com.ovidium.comoriod.views.article.ArticleViewContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ODRefsBottomView(
    state: Resource<BibleChapter?>,
    libraryModel: LibraryModel,
    signInModel: GoogleSignInModel,
    favoritesModel: FavoritesModel,
    searchModel: SearchModel,
    markupsModel: MarkupsModel,
    readArticlesModel: ReadArticlesModel,
    currentAuthor: MutableState<CountedAuthor?>,
    odReferenceData: Map<String, ODRef>,
) {

    val receivedMarkupIndex = remember { mutableStateOf(0) }
    val receivedMarkupLength = remember { mutableStateOf(0) }

    val filteredODRefs = odReferenceData.values.filter { it.author == currentAuthor.value?.name }
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { filteredODRefs.size }
    )
    val showFullArticle = remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (showFullArticle.value) {
                val currentODRef = filteredODRefs[pagerState.currentPage]
                ArticleView(
                    articleID = currentODRef.articleID,
                    markupId = null,
                    isSearch = null,
                    receivedMarkupIndex = receivedMarkupIndex,
                    receivedMarkupLength = receivedMarkupLength,
                    signInModel = signInModel,
                    favoritesModel = favoritesModel,
                    searchModel = searchModel,
                    markupsModel = markupsModel,
                    readArticlesModel = readArticlesModel,
                    bgColor = getNamedColor("HeaderBar", isSystemInDarkTheme())
                )
            } else {
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
                                ODRefBasicTextView(filteredODRefs, pageIDX)
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                showFullArticle.value = !showFullArticle.value
            },
            backgroundColor = getNamedColor("Link", isSystemInDarkTheme()),
            modifier = Modifier
                .padding(end = 16.dp)
                .height(50.dp)
                .width(35.dp)
                .alpha(0.8f)
        ) {
            Column {
                Icon(
                    if (showFullArticle.value) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    "Show full article floating action button"
                )
                Icon(
                    if (showFullArticle.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    "Show full article floating action button"
                )
            }
        }
    }
}

@Composable
private fun ODRefBasicTextView(filteredODRefs: List<ODRef>, pageIDX: Int) {
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
        Text(
            text = filteredODRefs[pageIDX].text,
            fontFamily = Montserrat,
            fontSize = 18.sp,
            fontWeight = FontWeight.Light
        )
    }
}