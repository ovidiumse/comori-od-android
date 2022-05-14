package com.ovidium.comoriod.views

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun FavoritesScreen(favoritesModel: FavoritesModel) {

    val favoriteArticles = remember { favoritesModel.favoriteArticlesData }

    Scaffold(
        topBar = {
            SearchTopBar(title = {Text(text = "Favorite")}, isSearch = false, onMenuClicked = { }) {

            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            LazyColumn() {
                favoriteArticles.value.data.let { articles ->
                    itemsIndexed(articles ?: emptyList()) { _, item ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            backgroundColor = getNamedColor("CornSilk", isSystemInDarkTheme())!!,
                            elevation = 1.dp,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Column() {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    color = getNamedColor("InvertedText", isSystemInDarkTheme())!!,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                Row(
                                    verticalAlignment = CenterVertically,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 16.dp)
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
                                        contentDescription = "Menu",
                                        tint = getNamedColor(
                                            "InvertedText",
                                            isSystemInDarkTheme()
                                        )!!,
                                        modifier = Modifier
                                            .size(16.dp)
                                    )
                                    Text(
                                        text = item.book,
                                        style = MaterialTheme.typography.caption,
                                        color = getNamedColor(
                                            "InvertedText",
                                            isSystemInDarkTheme()
                                        )!!,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}