package com.ovidium.comoriod.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.favorites.DeleteFavoriteConfirmationDialog
import com.ovidium.comoriod.views.favorites.FavoriteArticleCell
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                    favoritesModel.updateFavorites()
                },
                dismissAction = { articleToDelete.value = "" }
            )
        }
    }

    LaunchedEffect(Unit) {
        favoritesModel.updateFavorites()
    }

}
