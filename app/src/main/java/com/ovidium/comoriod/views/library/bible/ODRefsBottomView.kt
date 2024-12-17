package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.ODRef
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.model.ReadArticlesModel
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.Montserrat
import com.ovidium.comoriod.ui.theme.NotoSans
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.article.ArticleView
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

    val currentRelatedODRef = remember { mutableStateOf("") }

    val receivedMarkupIndex = remember { mutableStateOf(0) }
    val receivedMarkupLength = remember { mutableStateOf(0) }
    val odRefForBibleRef by libraryModel.odRef.collectAsState(Resource.loading(emptyList()))

    val filteredODRefs = odReferenceData.values.filter { it.author == currentAuthor.value?.name }
    val scrollState = rememberScrollState()
    val emptyODRefs by remember { mutableStateOf(mutableListOf<String>()) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { odRefForBibleRef.data?.size ?: 0 }
    )
    val showFullArticle = remember { mutableStateOf(false) }
    val expandRelatedODRefs = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(start = 8.dp, end = 8.dp)
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
                        val annotations =
                            currentVerse.formatedReference.getStringAnnotations(start = 0, end = currentVerse.formatedReference.text.length)
                        LazyHorizontalGrid(
                            rows = GridCells.Fixed(1),
                            modifier = Modifier
                                .height(50.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .border(
                                            if (currentRelatedODRef.value == currentVerse.verseTitle) 1.dp else 0.dp,
                                            if (currentRelatedODRef.value == currentVerse.verseTitle) getNamedColor(
                                                "Text",
                                                isSystemInDarkTheme()
                                            ) else Color.Transparent,
                                            RoundedCornerShape(25.dp)
                                        )
                                        .background(getNamedColor("grayBubble", isSystemInDarkTheme()), RoundedCornerShape(25.dp))
                                        .fillMaxHeight()
                                        .padding(start = 8.dp, end = 8.dp)
                                        .padding(top = 6.5.dp)
                                        .clickable {
                                            if (currentRelatedODRef.value != currentVerse.verseTitle) {
                                                currentRelatedODRef.value = currentVerse.verseTitle
                                                libraryModel.getOfRefFor(currentVerse.verseTitle)
                                            }
                                        }
                                ) {
                                    Text(
                                        text = currentVerse.verseTitle,
                                        fontSize = 10.sp,
                                        color = getNamedColor("Text", isSystemInDarkTheme())
                                    )
                                }
                            }
                            if (expandRelatedODRefs.value) {
                                items(annotations.size) { index ->
                                    Column(
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .border(
                                                if (currentRelatedODRef.value == annotations[index].item) 1.dp else 0.dp,
                                                if (currentRelatedODRef.value == annotations[index].item) getNamedColor(
                                                    "Text",
                                                    isSystemInDarkTheme()
                                                ) else Color.Transparent,
                                                RoundedCornerShape(25.dp)
                                            )
                                            .background(
                                                if (emptyODRefs.contains(annotations[index].item)) {
                                                    Color.Gray
                                                } else {
                                                    getNamedColor("Link", isSystemInDarkTheme())
                                                },
                                                RoundedCornerShape(25.dp)
                                            )
                                            .fillMaxHeight()
                                            .padding(start = 8.dp, end = 8.dp)
                                            .padding(top = 6.5.dp)
                                            .clickable {
                                                if (currentRelatedODRef.value != annotations[index].item) {
                                                    currentRelatedODRef.value = annotations[index].item
                                                    libraryModel.getOfRefFor(annotations[index].item)
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = annotations[index].item,
                                            fontSize = 10.sp,
                                            color = getNamedColor("InvertedText", isSystemInDarkTheme())
                                        )
                                    }
                                }
                            }
                            item {
                                Icon(
                                    imageVector = if (expandRelatedODRefs.value) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Show full article floating action button",
                                    modifier = Modifier
                                        .clickable {
                                            expandRelatedODRefs.value = !expandRelatedODRefs.value
                                            if (currentRelatedODRef.value != currentVerse.verseTitle) {
                                                currentRelatedODRef.value = currentVerse.verseTitle
                                            }
                                        },
                                    tint = getNamedColor("Link", isSystemInDarkTheme())
                                )
                            }
                        }

                        when (odRefForBibleRef.status) {
                            Status.SUCCESS -> {
                                if (!odRefForBibleRef.data.isNullOrEmpty()) {
                                    ODRefsBottomSheetAuthorCarousel(
                                        libraryModel,
                                        currentVerse,
                                        bibleChapter,
                                        currentAuthor,
                                        currentRelatedODRef.value,
                                        isSystemInDarkTheme()
                                    ) {
                                    }
                                    Text(
                                        modifier = Modifier
                                            .padding(bottom = 12.dp),
                                        text = "${currentAuthor.value?.name} - ${pagerState.currentPage + 1}/${odRefForBibleRef.data?.size ?: 0}",
                                        fontSize = 12.sp
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
                                            ODRefBasicTextView(odRefForBibleRef.data as List<ODRef>, pageIDX)
                                        }
                                    }
                                } else {
                                    coroutineScope.run {
                                        launch {
                                            when (odRefForBibleRef.status) {
                                                Status.SUCCESS -> {
                                                    if (!emptyODRefs.contains(currentRelatedODRef.value)) {
                                                        emptyODRefs.add(currentRelatedODRef.value)
                                                    }
                                                }
                                                else -> {}
                                            }
                                        }
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Top,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 64.dp)
                                    ) {
                                        Text(
                                            text = "Nu există referințe în scrierile \nînaintașilor pentru acest verset.",
                                            fontSize = 16.sp,
                                            fontFamily = NotoSans,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }

                            Status.ERROR,
                            Status.LOADING,
                            Status.UNINITIALIZED -> {}
                        }

                        LaunchedEffect(Unit) {
                            emptyODRefs.clear()
                            if (currentRelatedODRef.value != currentVerse.verseTitle) {
                                currentRelatedODRef.value = currentVerse.verseTitle
                                libraryModel.getOfRefFor(currentVerse.verseTitle, currentAuthor.value?.name)
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
private fun ODRefBasicTextView(
    filteredODRefs: List<ODRef>,
    pageIDX: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
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