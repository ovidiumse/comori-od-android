@file:OptIn(ExperimentalTime::class)

package com.ovidium.comoriod.views.article

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.CustomTextToolbar
import com.ovidium.comoriod.components.selection.SelectionContainer
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.model.*
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.favorites.SaveFavoriteDialog
import com.ovidium.comoriod.views.markups.SaveMarkupDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime

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
    markupsModel: MarkupsModel
) {
    val isDark = isSystemInDarkTheme()

    val articleModel: ArticleModel = viewModel()

    val listState = rememberLazyListState()
    val bibleRefs = articleModel.getBibleRefs(article.id)
    var showSaveFavoriteDialog by remember { mutableStateOf(false) }
    val markupSelection = remember { mutableStateOf("") }
    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    val showHighlightControls = remember { mutableStateOf(false) }
    val startPos = remember { mutableStateOf(0) }
    val endPos = remember { mutableStateOf(0) }
    val scrollOffset = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val mutedTextColor = getNamedColor("MutedText", isDark)
    val textColor = getNamedColor("Text", isDark)
    val bgColor = getNamedColor("Background", isDark)
    val handleColor = getNamedColor("HandleColor", isDark)

    Box(modifier = Modifier.background(bgColor)) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                state = listState
            ) {
                item {
                    Text(
                        text = article.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = getNamedColor("OnBackground", isDark)
                    )
                }
                item {
                    ArticleInfoView(article, mutedTextColor)
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
                        showHighlightControls
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
                            showDeleteFavoriteDialog = true
                        } else {
                            showSaveFavoriteDialog = true
                        }
                    },
                    modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                    backgroundColor = if (favoritesModel.isFavorite(article.id)
                    ) Color.Red else getNamedColor("Link", isDark)
                ) {
                    if (favoritesModel.isFavorite(article.id)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_delete_24),
                            contentDescription = "Delete",
                            tint = getNamedColor("Container", isDark),
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_star_24),
                            contentDescription = "Favorite",
                            tint = getNamedColor("Container", isDark),
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
                            coroutineScope.launch { listState.animateScrollToItem(2, offsetList[currentHighlightIndex.value!!]) }
                        } else {
                            currentHighlightIndex.value = 0
                            coroutineScope.launch { listState.animateScrollToItem(2, offsetList[currentHighlightIndex.value!!]) }
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
                            coroutineScope.launch { listState.animateScrollToItem(2, offsetList[currentHighlightIndex.value!!]) }
                        } else {
                            currentHighlightIndex.value = 0
                            coroutineScope.launch { listState.animateScrollToItem(2, offsetList[currentHighlightIndex.value!!]) }
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

    if (showDeleteFavoriteDialog) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = "Atenție")
            },
            text = {
                Text("Ești sigur că vrei să ștergi acest articol favorit?")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    ),
                    onClick = {
                        favoritesModel.deleteFavoriteArticle(article.id)
                        showDeleteFavoriteDialog = false
                    }) {
                    Text("Șterge")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = getNamedColor("Link", isDark),
                        contentColor = Color.White
                    ),
                    onClick = {
                        showDeleteFavoriteDialog = false
                    }) {
                    Text("Renunță")
                }
            }
        )
    }

    LaunchedEffect(listState) {
        if (scrollOffset.value != 0)
            listState.scrollToItem(2, scrollOffset.value)
    }
}