package com.ovidium.comoriod.views

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.components.NoContentPlaceholder
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.favorites.DeleteFavoriteConfirmationDialog
import com.ovidium.comoriod.views.favorites.FavoriteArticleCell
import com.ovidium.comoriod.views.favorites.SwipeableFavoriteArticleCell
import java.net.URLEncoder

@Composable
fun FavoritesScreen(
    navController: NavController,
    favoritesModel: FavoritesModel,
    scaffoldState: ScaffoldState
) {
    val favoritesData = favoritesModel.favorites
    val tags = favoritesData.value.data?.map { article -> article.tags }?.flatten()?.distinct()
        ?.filter { tag -> tag.isNotEmpty() }
        ?: emptyList()

    val isDark = isSystemInDarkTheme()
    val surfaceColor = getNamedColor("PrimarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)

    val coroutineScope = rememberCoroutineScope()
    var selectedTag by remember { mutableStateOf("") }

    fun getArticles(): List<FavoriteArticle>? {
        // selectedTag may not be in the list of tags after a deletion
        if (!tags.contains(selectedTag))
            selectedTag = ""

        return if (selectedTag.isEmpty())
            favoritesData.value.data?.reversed()
        else
            favoritesData.value.data?.reversed()?.filter { fav -> fav.tags.contains(selectedTag) }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Favorite",
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState) }) {
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background) //?
        ) {
            when (favoritesData.value.status) {
                Status.SUCCESS -> {
                    val favorites = getArticles()
                    if (!favorites.isNullOrEmpty()) {
                        TagsRow(
                            tags,
                            selectedTag,
                            onTagsChanged = { tag -> selectedTag = tag })

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            favorites.forEachIndexed { index, article ->
                                item(key = article.id) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp)
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
                    } else {
                        NoContentPlaceholder("Nu ai nici un articol favorit")
                    }
                }
                else -> {}
            }
        }
    }
}


@Composable
fun TagsRow(
    tags: List<String>,
    selectedTag: String,
    onTagsChanged: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    LazyRow(
        modifier = Modifier
            .padding(12.dp)
    ) {
        item {
            CapsuleButton(
                text = "Toate",
                isDark = isDark,
                isSelected = selectedTag.isEmpty(),
                action = {
                    onTagsChanged("")
                }
            )
        }

        tags.forEach { tag ->
            item {
                CapsuleButton(
                    text = tag,
                    isDark = isDark,
                    isSelected = selectedTag == tag,
                    action = { tag -> onTagsChanged(tag) }
                )
            }
        }
    }
}

@Composable
fun CapsuleButton(text: String, isDark: Boolean, isSelected: Boolean, action: (String) -> Unit) {

    val bubbleColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("HeaderText", isDark)
    val unselectedText = getNamedColor("MutedText", isDark)

    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = MaterialTheme.typography.caption.fontSize,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) textColor else unselectedText,
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                bubbleColor.copy(alpha = if (isSelected) 1.0f else 0.3f),
                shape = MaterialTheme.shapes.medium,
            )
            .padding(12.dp)
            .clickable { action(text) }
            .defaultMinSize(minWidth = 60.dp)
    )
}