package com.ovidium.comoriod.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.components.NoContentPlaceholder
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.favorites.DeleteFavoriteConfirmationDialog
import com.ovidium.comoriod.views.favorites.FavoriteArticleCell

@Composable
fun FavoritesScreen(
    navController: NavController,
    favoritesModel: FavoritesModel,
    scaffoldState: ScaffoldState
) {
    val favoritesData = favoritesModel.articles
    val tags = favoritesData.value.data?.map { article -> article.tags }?.flatten()?.distinct()
        ?.filter { tag -> tag.isNotEmpty() }
        ?: emptyList()

    val articleToDelete = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var selectedTag by remember { mutableStateOf("") }

    fun getArticles(): List<FavoriteArticle>? {
        // selectedTag may not be in the list of tags after a deletion
        if (!tags.contains(selectedTag))
            selectedTag = ""

        return if (selectedTag.isEmpty())
            favoritesData.value.data
        else
            favoritesData.value.data?.filter { fav -> fav.tags.contains(selectedTag) }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                title = { Text(text = "Favorite") },
                isSearch = false,
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState) }) {
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            when (favoritesData.value.status) {
                Status.SUCCESS -> {
                    val favorites = getArticles()
                    if (!favorites.isNullOrEmpty()) {
                    TagsRow(
                        tags,
                        selectedTag,
                        onTagsChanged = { tag -> selectedTag = tag })

                    LazyColumn() {
                        favorites.forEach { article ->
                            item() {
                                FavoriteArticleCell(navController, article) { idToDelete ->
                                    articleToDelete.value = idToDelete
                                }
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

        if (articleToDelete.value.isNotEmpty()) {
            DeleteFavoriteConfirmationDialog(
                deleteAction = {
                    favoritesModel.deleteFavoriteArticle(articleToDelete.value)
                    articleToDelete.value = ""
                },
                dismissAction = { articleToDelete.value = "" }
            )
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
            .padding(16.dp)
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
    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.caption,
        color = if (isSelected) Color.White else getNamedColor("Text", isDark = isDark)!!,
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                if (isSelected) getNamedColor("Link", isDark)!! else getNamedColor(
                    "PopupContainer",
                    isDark
                )!!.copy(alpha = 0.3f),
                RoundedCornerShape(50)
            )
            .padding(12.dp)
            .clickable { action(text) }
            .defaultMinSize(minWidth = 60.dp)
    )
}