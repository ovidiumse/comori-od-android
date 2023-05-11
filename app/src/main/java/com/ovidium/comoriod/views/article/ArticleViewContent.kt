@file:OptIn(ExperimentalTime::class)

package com.ovidium.comoriod.views.article

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.AdaptiveText
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.data.article.ReadArticle
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.model.*
import com.ovidium.comoriod.ui.theme.NotoSans
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.fmtDuration
import com.ovidium.comoriod.utils.nowUtc
import com.ovidium.comoriod.utils.toIsoString
import com.ovidium.comoriod.views.favorites.SaveFavoriteDialog
import com.ovidium.comoriod.views.markups.SaveMarkupDialog
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.time.ExperimentalTime

@Composable
private fun LazyListState.isAtBottom(): Boolean {
    return remember(this) {
        derivedStateOf {
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()
                val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

                (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
                        lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight)
            }
        }
    }.value
}

@Composable
fun ArticleViewContent(
    article: Article,
    markupId: String?,
    isSearch: String?,
    markups: List<Markup>,
    highlights: SnapshotStateList<TextRange>,
    offsetList: SnapshotStateList<Int>,
    currentHighlightIndex: MutableState<Int?>,
    signInModel: GoogleSignInModel,
    favoritesModel: FavoritesModel,
    markupsModel: MarkupsModel,
    readArticlesModel: ReadArticlesModel
) {
    val isDark = isSystemInDarkTheme()

    val articleModel: ArticleModel = viewModel()

    var readTimePassed by remember { mutableStateOf(false) }
    var bottomReached by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val bibleRefs = articleModel.getBibleRefs(article.id)
    var showSaveFavoriteDialog by remember { mutableStateOf(false) }
    val markupSelection = remember { mutableStateOf("") }
    val showHighlightControls = remember { mutableStateOf(false) }
    val startPos = remember { mutableStateOf(0) }
    val endPos = remember { mutableStateOf(0) }
    val scrollOffset = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val mutedTextColor = getNamedColor("MutedText", isDark)
    val doneColor = getNamedColor("doneColor", isDark)
    val textColor = getNamedColor("Text", isDark)
    val bgColor = getNamedColor("Background", isDark)
    val primarySurfaceColor = getNamedColor("PrimarySurface", isDark)
    val buttonColor = getNamedColor("PrimarySurface", isDark)
    val handleColor = getNamedColor("HandleColor", isDark)

    if (favoritesModel.favorites.value.status == Status.UNINITIALIZED && signInModel.userResource.value.state == UserState.LoggedIn)
        favoritesModel.loadFavorites()

    if (markupsModel.markups.value.status == Status.UNINITIALIZED && signInModel.userResource.value.state == UserState.LoggedIn)
        markupsModel.loadMarkups()

    val hasPopup = markupSelection.value.isNotEmpty() || showSaveFavoriteDialog;
    val bgColorState = animateColorAsState(
        targetValue = if (hasPopup) primarySurfaceColor else bgColor,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .background(bgColorState.value)
            .blur(if (markupSelection.value.isNotEmpty() || showSaveFavoriteDialog) 16.dp else 0.dp)
    ) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = listState
            ) {
                item {
                    AdaptiveText(
                        text = article.title,
                        minFontSize = 24.sp,
                        maxFontSize = 38.sp,
                        fontFamily = NotoSans,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp),
                        color = getNamedColor("OnBackground", isDark)
                    )
                }
                item {
                    ArticleInfoView(article, mutedTextColor, readArticlesModel)
                }

                item {
                    when (readArticlesModel.readArticles.value.status) {
                        Status.SUCCESS -> {
                            val readArticle =
                                readArticlesModel.readArticles.value.data?.lastOrNull { readArticle -> readArticle.id == article.id }
                            if (readArticle != null) {
                                val date = ZonedDateTime.parse(readArticle.timestamp, DateTimeFormatter.ISO_DATE_TIME)
                                val duration = Duration.between(date.toInstant(), Instant.now())

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        imageVector = ImageVector.vectorResource(id = if (readArticle.count > 1) R.drawable.baseline_done_all_24 else R.drawable.baseline_done_24),
                                        contentDescription = "Read time",
                                        tint = doneColor.copy(alpha = 0.7f)
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    val prefix = if (readArticle.count > 1) "recitit " else "citit "
                                    Text(prefix + fmtDuration(duration), color = mutedTextColor)
                                }
                            }
                        }
                        Status.LOADING -> {

                        }
                        else -> {}
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
                            contentDescription = "Read time",
                            tint = mutedTextColor.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        var durationText = fmtDuration(Duration.ofSeconds(article.read_time.toLong()), "")
                        if (durationText.isEmpty())
                            durationText = "< 1 min"

                        Text("$durationText de citit", color = mutedTextColor)
                    }
                }

                item {
                    ArticleBodyView(
                        article,
                        markupSelection,
                        markups,
                        highlights,
                        offsetList,
                        currentHighlightIndex,
                        markupId,
                        textColor,
                        handleColor,
                        startPos,
                        endPos,
                        scrollOffset,
                        bibleRefs,
                        showHighlightControls,
                        signInModel
                    )
                }
            }
        }

        if (bibleRefs.isNotEmpty()) {
            BibleRefsPopup(bibleRefs)
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
        ) {
            val userResourceState = signInModel.userResource
            val userResource = userResourceState.value
            if (userResource.state == UserState.LoggedIn)
                FloatingActionButton(
                    onClick = {
                        if (favoritesModel.isFavorite(article.id)) {
                            favoritesModel.deleteFavoriteArticle(article.id)
                        } else {
                            showSaveFavoriteDialog = true
                        }
                    },
                    modifier = Modifier.padding(bottom = 100.dp, end = 16.dp),
                    backgroundColor = buttonColor
                ) {
                    if (favoritesModel.isFavorite(article.id)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_favorite_24),
                            contentDescription = "Remove from favorites",
                            modifier = Modifier.size(35.dp),
                            tint = Color.Red,
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_favorite_border_24),
                            contentDescription = "Mark as favorite",
                            modifier = Modifier.size(35.dp),
                            tint = Color.Black,
                        )
                    }
                }
        }

        //Highlights
        if (showHighlightControls.value && offsetList.isNotEmpty() && isSearch == "true") {
            println("Show highlights: ${highlights.count()}")
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                FloatingActionButton(
                    onClick = {
                        if (currentHighlightIndex.value != null) {
                            if (currentHighlightIndex.value!! > 0) {
                                currentHighlightIndex.value = currentHighlightIndex.value!! - 1
                            } else {
                                currentHighlightIndex.value = highlights.size - 1
                            }
                            coroutineScope.launch {
                                listState.animateScrollToItem(
                                    2,
                                    offsetList[currentHighlightIndex.value!!]
                                )
                            }
                        } else {
                            currentHighlightIndex.value = 0
                            coroutineScope.launch {
                                listState.animateScrollToItem(
                                    2,
                                    offsetList[currentHighlightIndex.value!!]
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                    backgroundColor = getNamedColor("SecondarySurface", isDark)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_up),
                        contentDescription = "Up",
                        tint = getNamedColor("Container", isDark)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        if (currentHighlightIndex.value != null) {
                            if (currentHighlightIndex.value!! < highlights.size - 1) {
                                currentHighlightIndex.value = currentHighlightIndex.value!! + 1
                            } else {
                                currentHighlightIndex.value = 0
                            }
                            coroutineScope.launch {
                                listState.animateScrollToItem(
                                    2,
                                    offsetList[currentHighlightIndex.value!!]
                                )
                            }
                        } else {
                            currentHighlightIndex.value = 0
                            coroutineScope.launch {
                                listState.animateScrollToItem(
                                    2,
                                    offsetList[currentHighlightIndex.value!!]
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 16.dp, top = 16.dp),
                    backgroundColor = getNamedColor("SecondarySurface", isDark)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "Down",
                        tint = getNamedColor("Container", isDark)
                    )
                }
            }
        }
    }

    if (showSaveFavoriteDialog) {
        SaveFavoriteDialog(
            articleToSave = article,
            onSaveAction = { tags ->
                val favoriteArticle = FavoriteArticle(
                    id = article.id,
                    title = article.title.text,
                    tags = tags,
                    author = article.author,
                    book = article.book,
                    timestamp = ""
                )
                favoritesModel.saveFavorite(favoriteArticle)
                showSaveFavoriteDialog = false
            },
            onExitAction = {
                showSaveFavoriteDialog = false
            }
        )
    }

    if (markupSelection.value.isNotEmpty()) {
        SaveMarkupDialog(
            articleToSave = article,
            selection = markupSelection.value,
            startPos = startPos.value,
            endPos = endPos.value,
            onSaveAction = { markup ->
                markupsModel.save(markup)
                markupSelection.value = ""
                startPos.value = 0
                endPos.value = 0
            },
            onExitAction = {
                markupSelection.value = ""
                startPos.value = 0
                endPos.value = 0
            }
        )
    }

    if (listState.isAtBottom())
        bottomReached = true

    if (readTimePassed && bottomReached) {
        readTimePassed = false
        bottomReached = false

        Log.d("ArticleViewContent", "Registering read article ${article.id}")
        readArticlesModel.addOrUpdate(
            ReadArticle(
                article.author,
                article.book,
                1,
                article.id,
                article.title.toString(),
                article.volume,
                toIsoString(nowUtc())
            )
        )
    }

    LaunchedEffect(article.id) {
        Log.d("ArticleViewContent", "Reading time: ${article.read_time}")
        Timer().schedule(timerTask {
            readTimePassed = true
            Log.d("ArticleViewContent", "Reading timer fired!")
        }, article.read_time * 1000L)
    }

    LaunchedEffect(listState) {
        if (scrollOffset.value != 0)
            listState.scrollToItem(2, scrollOffset.value)
    }
}

