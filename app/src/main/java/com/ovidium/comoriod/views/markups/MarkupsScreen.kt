package com.ovidium.comoriod.views.markups

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.components.NoContentPlaceholder
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.TagsRow
import com.ovidium.comoriod.views.search.SearchBar
import com.ovidium.comoriod.views.unaccent
import java.net.URLEncoder

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MarkupsScreen(
    navController: NavController,
    markupsModel: MarkupsModel,
    signInModel: GoogleSignInModel,
    scaffoldState: ScaffoldState
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = getNamedColor("Background", isDark)
    val surfaceColor = getNamedColor("PrimarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)

    val markupsData = markupsModel.markups

    if (markupsData.value.status == Status.UNINITIALIZED && signInModel.userResource.value.state == UserState.LoggedIn)
        markupsModel.loadMarkups()

    var showSearchBar by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var query by remember { mutableStateOf("") }
    var searchTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                query,
                TextRange(query.length)
            )
        )
    }
    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val tags =
        markupsData.value.data?.reversed()?.map { markup -> markup.tags }?.flatten()?.distinct()
            ?.filter { tag -> tag.isNotEmpty() }
            ?: emptyList()

    val coroutineScope = rememberCoroutineScope()
    var selectedTag by remember { mutableStateOf("") }

    fun getMarkups(): List<Markup>? {
        if (!tags.contains(selectedTag))
            selectedTag = ""

        return if (selectedTag.isEmpty())
            markupsData.value.data?.reversed()?.filter { mark ->
                mark.tags.joinToString().unaccent().contains(query.unaccent().lowercase())
                        ||
                        mark.title.lowercase().unaccent().contains(query.unaccent().lowercase())
                        ||
                        mark.selection.lowercase().unaccent().contains(query.unaccent().lowercase())
            }
        else
            markupsData.value.data?.reversed()?.filter { mark ->
                mark.tags.contains(selectedTag)
                        &&
                        (mark.tags.joinToString().unaccent().contains(query.unaccent().lowercase())
                                ||
                                mark.title.unaccent().lowercase().contains(query.unaccent().lowercase())
                                ||
                                mark.selection.unaccent().lowercase().contains(query.unaccent().lowercase()))
            }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Pasaje",
                onTitleClicked = {
                    navController.navigate(Screens.Library.route) {
                        launchSingleTop = true
                    }
                },
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState.drawerState) },
                actions = @Composable {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.clickable(onClick = {
                            showSearchBar = true
                        }),
                        tint = getNamedColor("HeaderText", isDark = isDark)
                    )
                })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {
            when (markupsData.value.status) {
                Status.SUCCESS -> {
                    val markups = getMarkups()

                    if (markups.isNullOrEmpty())
                        NoContentPlaceholder("Nu ai nici un pasaj favorit")
                    else {
                        AnimatedVisibility(
                            visible = showSearchBar,
                            enter = slideInVertically {
                                with(density) { -40.dp.roundToPx() }
                            } + expandVertically(
                                expandFrom = Alignment.Top
                            ) + fadeIn(
                                initialAlpha = 0.1f
                            ),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut()
                        ) {

                            SearchBar(
                                modifier = Modifier
                                    .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                    .fillMaxWidth(),
                                searchText = searchTextFieldValue,
                                shouldFocus = false,
                                focusRequester = focusRequester,
                                onSearchTextChanged = { newFieldValue ->
                                    searchTextFieldValue = newFieldValue
                                    query = newFieldValue.text
                                },
                                onSearchClick = {
                                    keyboardController?.hide()
                                },
                                onClearClick = {
                                    query = ""
                                    searchTextFieldValue = TextFieldValue(query)
                                    keyboardController?.hide()
                                    showSearchBar = false
                                }
                            )
                        }
                        TagsRow(
                            tags,
                            selectedTag,
                            onTagsChanged = { tag -> selectedTag = tag })

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            markups.forEachIndexed { index, markup ->
                                item(key = markup.id) {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp)
                                            .animateItemPlacement(
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                    ) {
                                        SwipeableMarkupCell(
                                            markup = markup,
                                            surfaceColor = surfaceColor,
                                            bubbleColor = bubbleColor,
                                            isDark = isDark,
                                            deleteAction = {
                                                markupsModel.deleteMarkup(markup.id)
                                            },
                                            onItemClick = {
                                                navController.navigate(
                                                    Screens.Article.withArgs(
                                                        "${
                                                            URLEncoder.encode(
                                                                markup.articleID,
                                                                "utf-8"
                                                            )
                                                        }?markupId=${URLEncoder.encode(markup.id, "utf-8")}"
                                                    )
                                                )
                                            })
                                    }

                                    if (index == markups.size - 1)
                                        Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}