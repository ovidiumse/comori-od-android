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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.views.favorites.DeleteFavoriteConfirmationDialog
import com.ovidium.comoriod.views.favorites.FavoriteArticleCell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Array.set
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

@Composable
fun FavoritesScreen(favoritesModel: FavoritesModel, scaffoldState: ScaffoldState) {

    val favoriteArticles by remember { favoritesModel.favoriteArticlesData }
    val articleToDelete = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()


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
            TagsRow(favoriteArticles, favoritesModel.selectedTags, onTagsChanged = { favoritesModel.filterArticles() })
            LazyColumn() {
                favoriteArticles.data?.let { articles ->
                itemsIndexed(if (favoritesModel.selectedTags.isEmpty()) articles else favoritesModel.filteredArticles) { _, favoriteArticle ->
                    FavoriteArticleCell(favoriteArticle) { idToDelete ->
                        articleToDelete.value = idToDelete
                    }
                }
                }
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
fun TagsRow(favoriteArticles: Resource<List<FavoriteArticle>?>, selectedTags: SnapshotStateList<String>, onTagsChanged: () -> Unit) {

    val isDark = isSystemInDarkTheme()

    LazyRow(
        modifier = Modifier
            .padding(16.dp)
    ) {
        val tags = favoriteArticles.data?.map { it.tags }?.flatten()?.distinct().let { it } ?: emptyList()
            item {
                CapsuleButton(
                    text = "Toate",
                    isDark = isDark,
                    isSelected = selectedTags.isEmpty(),
                    action = {
                        selectedTags.clear()
                        onTagsChanged()
                    }
                )
            }
        repeat(tags.count()) { index ->
            item {
                CapsuleButton(
                    text = tags[index],
                    isDark = isDark,
                    isSelected = selectedTags.contains(tags[index]),
                    action = { tag ->
                        if (selectedTags.contains(tag)) {
                            selectedTags.remove(tag)
                        } else {
                            selectedTags.add(tag)
                        }
                        onTagsChanged()
                    }
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