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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.components.NoContentPlaceholder
import com.ovidium.comoriod.components.TagsRow
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.Screens
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

    var showSearchBar by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var query by rememberSaveable { mutableStateOf("") }
    var searchTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                query,
                TextRange(query.length)
            )
        )
    }

    val filteredMarkups = markupsData.value.data?.filter { markup ->
        val filterQuery = query.lowercase().unaccent()
        fun match(input: String): Boolean {
            return input.lowercase().unaccent().contains(filterQuery)
        }

        markup.tags.any { tag -> match(tag) } || match(markup.title) || match(markup.selection)
    }

    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val tags = filteredMarkups?.reversed()?.map { article -> article.tags }?.flatten()?.distinct()
            ?.filter { tag -> tag.isNotEmpty() }
            ?: emptyList()

    val tagCounts: MutableMap<String, Int> = mutableMapOf()
    filteredMarkups?.forEach { markup ->
        markup.tags.forEach { tag ->
            if (tag.isNotEmpty()) {
                tagCounts.putIfAbsent(tag, 0)
                tagCounts[tag] = tagCounts[tag]!! + 1
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var selectedTag by rememberSaveable { mutableStateOf("") }

    fun getMarkups(): List<Markup>? {
        if (!tags.contains(selectedTag))
            selectedTag = ""

        return if (selectedTag.isEmpty())
            filteredMarkups?.reversed()
        else
            filteredMarkups?.reversed()?.filter { markup -> markup.tags.contains(selectedTag) }
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
                    if (!markupsData.value.data.isNullOrEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.clickable(onClick = {
                                showSearchBar = true
                            }),
                            tint = getNamedColor("HeaderText", isDark = isDark)
                        )
                    }
                })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {
            if (!markupsData.value.data.isNullOrEmpty()) {
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
                        shouldFocus = true,
                        placeholderText = "Caută în marcaje...",
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
            }

            when (markupsData.value.status) {
                Status.SUCCESS -> {
                    if (markupsData.value.data.isNullOrEmpty()) {
                        NoContentPlaceholder("Nu ai nici un pasaj favorit")
                    }
                    else if (filteredMarkups.isNullOrEmpty()) {
                        NoContentPlaceholder("Nici un pasaj favorit găsit")
                    }
                    else {
                        val markups = getMarkups()

                        TagsRow(
                            tags,
                            tagCounts,
                            filteredMarkups.size,
                            selectedTag,
                            onTagsChanged = { tag -> selectedTag = tag })

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            markups?.forEachIndexed { index, markup ->
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
                                            highlight = query,
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