package com.ovidium.comoriod.views.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.Screens
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun FavoriteArticleCell(
    navController: NavController,
    favoriteArticle: FavoriteArticle,
    deleteAction: (String) -> Unit
) {

    val isDark = isSystemInDarkTheme()
    val tagBG = getNamedColor("SecondarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("HeaderText", isDark)

    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = bubbleColor,
        elevation = 1.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .clickable { navController.navigate(Screens.Article.withArgs(favoriteArticle.id)) }
        ) {
            Text(
                text = favoriteArticle.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            FavoriteCellTitle(favoriteArticle, textColor)
            if (favoriteArticle.tags.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    favoriteArticle.tags.forEach { tag ->
                        if (tag.isNotEmpty())
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.caption,
                                color = textColor,
                                modifier = Modifier
                                    .padding(start = 12.dp, end = 8.dp)
                                    .padding(bottom = 8.dp)
                                    .background(
                                        tagBG.copy(alpha = 0.5f),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(8.dp)
                            )
                    }
                }
            }
            FavoriteCellInfo(favoriteArticle, tagBG, textColor, deleteAction)
        }
    }
}


@Composable
fun FavoriteCellTitle(favoriteArticle: FavoriteArticle, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
            contentDescription = "Menu",
            tint = textColor,
            modifier = Modifier
                .size(16.dp)
        )
        Text(
            text = favoriteArticle.book,
            style = MaterialTheme.typography.caption,
            color = textColor,
            modifier = Modifier
                .padding(start = 8.dp)
        )

    }
}


@Composable
fun FavoriteCellInfo(favoriteArticle: FavoriteArticle, bgColor: Color, textColor: Color, deleteAction: (String) -> Unit) {
    Column(
        modifier = Modifier
            .background(bgColor.copy(alpha = 0.4f))
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_access_time_24),
                        contentDescription = "Menu",
                        tint = textColor,
                        modifier = Modifier
                            .padding(end = 5.dp)
                    )
                    var inFormatter = DateTimeFormatter.ISO_DATE_TIME
                    val rawDate = LocalDate.parse(favoriteArticle.timestamp, inFormatter)
                    val formattedDate = "${rawDate.dayOfMonth} ${rawDate.month} - ${rawDate.year}"
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.caption,
                        color = textColor
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
                    tint = textColor,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable { deleteAction(favoriteArticle.id) }
                )
            }
        }
    }
}