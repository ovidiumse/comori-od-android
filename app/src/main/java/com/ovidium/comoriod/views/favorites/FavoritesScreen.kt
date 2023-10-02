package com.ovidium.comoriod.views

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.components.NoContentPlaceholder
import com.ovidium.comoriod.components.TagsRow
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.favorites.SwipeableFavoriteArticleCell
import com.ovidium.comoriod.views.search.SearchBar
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.text.Normalizer


val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun CharSequence.unaccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "")
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FavoritesScreen(
    navController: NavController,
    favoritesModel: FavoritesModel,
    signInModel: GoogleSignInModel,
    scaffoldState: ScaffoldState
) {
    val favoritesData = favoritesModel.favorites

    if (favoritesData.value.status == Status.UNINITIALIZED && signInModel.userResource.value.state == UserState.LoggedIn)
        favoritesModel.loadFavorites()

    val isDark = isSystemInDarkTheme()
    val surfaceColor = getNamedColor("PrimarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)

    val coroutineScope = rememberCoroutineScope()
    var selectedTag by rememberSaveable { mutableStateOf("") }
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

    val filteredFavorites = favoritesData.value.data?.filter { fav ->
        val filterQuery  = query.lowercase().unaccent()
        fun match(input: String): Boolean {
            return input.lowercase().unaccent().contains(filterQuery)
        }

        fav.tags.any { tag -> match(tag) } || match(fav.title)
    }

    val tags = filteredFavorites?.reversed()?.map { article -> article.tags }?.flatten()?.distinct()
            ?.filter { tag -> tag.isNotEmpty() }
            ?: emptyList()

    val tagCounts: MutableMap<String, Int> = mutableMapOf();
    filteredFavorites?.forEach { fav ->
        fav.tags.forEach { tag ->
            if (tag.isNotEmpty()) {
                tagCounts.putIfAbsent(tag, 0)
                tagCounts[tag] = tagCounts[tag]!! + 1
            }
        }
    }

    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun getFavoriteArticles(): List<FavoriteArticle>? {
        // selectedTag may not be in the list of tags after a deletion
        if (!tags.contains(selectedTag))
            selectedTag = ""

        return if (selectedTag.isEmpty())
            filteredFavorites?.reversed()
        else
            filteredFavorites?.reversed()?.filter { fav -> fav.tags.contains(selectedTag) }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Favorite",
                onTitleClicked = {
                    navController.navigate(Screens.Library.route) {
                        launchSingleTop = true
                    }
                },
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState.drawerState) },
                actions = @Composable {
                    if (!favoritesData.value.data.isNullOrEmpty()) {
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
                .background(MaterialTheme.colors.background) //?
        ) {
            if (!favoritesData.value.data.isNullOrEmpty()) {
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
                        placeholderText = "Caută în titlurile favorite...",
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

            when (favoritesData.value.status) {
                Status.SUCCESS -> {
                    if (favoritesData.value.data.isNullOrEmpty()) {
                        NoContentPlaceholder("Nu ai nici un articol favorit")
                    }
                    else if (filteredFavorites.isNullOrEmpty()) {
                        NoContentPlaceholder("Nici un articol favorit găsit")
                    }
                    else {
                        val favorites = getFavoriteArticles()
                        TagsRow(
                            tags,
                            tagCounts,
                            filteredFavorites.size,
                            selectedTag,
                            onTagsChanged = { tag -> selectedTag = tag })
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            favorites?.forEachIndexed { index, article ->
                                item(key = article.id) {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp)
                                            .animateItemPlacement(
                                                animationSpec = tween(durationMillis = 300)
                                            )
                                    ) {
                                        SwipeableFavoriteArticleCell(
                                            favoriteArticle = article,
                                            isDark = isDark,
                                            surfaceColor = surfaceColor,
                                            bubbleColor = bubbleColor,
                                            onItemClick = {
                                                navController.navigate(
                                                    Screens.Article.withArgs(URLEncoder.encode(article.id, "utf-8"))
                                                )
                                            },
                                            deleteAction = {
                                                favoritesModel.deleteFavoriteArticle(article.id)
                                            })
                                    }

                                    if (index == favorites.size - 1)
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
