package com.ovidium.comoriod.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.internal.bind.util.ISO8601Utils
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun FavoritesScreen(favoritesModel: FavoritesModel) {

    val favoriteArticles = remember { favoritesModel.favoriteArticlesData }

    Scaffold(
        topBar = {
            SearchTopBar(
                title = { Text(text = "Favorite") },
                isSearch = false,
                onMenuClicked = { }) {

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
                    itemsIndexed(articles ?: emptyList()) { _, favoriteArticle ->
                        FavoriteArticleCell(favoriteArticle)
                    }
                }
            }
        }
    }
}


@Composable
fun FavoriteArticleCell(favoriteArticle: FavoriteArticle) {
    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = getNamedColor("CornSilk", isSystemInDarkTheme())!!,
        elevation = 1.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column {
            Text(
                text = favoriteArticle.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = getNamedColor("InvertedText", isSystemInDarkTheme())!!,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            FavoriteCellTitle(favoriteArticle)
            FavoriteCellInfo(favoriteArticle)
        }
    }
}



@Composable
fun FavoriteCellTitle(favoriteArticle: FavoriteArticle) {
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
            text = favoriteArticle.book,
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



@Composable
fun FavoriteCellInfo(favoriteArticle: FavoriteArticle) {
    Column(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.Start) {

                Row(
                    verticalAlignment = CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_tag_24),
                        contentDescription = "Tag",
                        modifier = Modifier
                            .padding(end = 5.dp)
                    )
                    repeat(favoriteArticle.tags.count()) {
                        Text(
                            text = favoriteArticle.tags[it],
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(Color.Red, RoundedCornerShape(50))
                                .padding(5.dp)
                        )
                    }
                }

                Row(verticalAlignment = CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_access_time_24),
                        contentDescription = "Menu",
                        modifier = Modifier
                            .padding(end = 5.dp)
                    )
                    var inFormatter = DateTimeFormatter.ISO_DATE_TIME
                    val rawDate = LocalDate.parse(favoriteArticle.timestamp, inFormatter)
                    val formattedDate = "${rawDate.dayOfMonth} ${rawDate.month} - ${rawDate.year}"
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.caption,
                    )
                }

            }
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_delete_24),
                    contentDescription = "Menu",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable { /* Delete favorite article */ }
                )
            }
        }
    }
}
