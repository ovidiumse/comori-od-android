package com.ovidium.comoriod.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.views.favorites.DeleteFavoriteConfirmationDialog
import com.ovidium.comoriod.views.favorites.FavoriteArticleCell

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            LazyColumn() {
                favoriteArticles.data?.let { articles ->
                    itemsIndexed(articles) { _, favoriteArticle ->
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
