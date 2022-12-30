package com.ovidium.comoriod.views.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.TagBubble
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.fmtDuration
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.markups.MarkupCell
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun FavoriteArticleCell(
    modifier: Modifier = Modifier,
    favoriteArticle: FavoriteArticle,
    isDark: Boolean,
    onItemClick: () -> Unit,
) {
    val containerColor = getNamedColor("Container", isDark)
    var bubbleColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("HeaderText", isDark)

    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = containerColor,
        elevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .clickable { onItemClick() }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = favoriteArticle.title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
                        contentDescription = "Menu",
                        tint = textColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${favoriteArticle.author} - ${favoriteArticle.book}",
                        style = MaterialTheme.typography.caption,
                        color = textColor.copy(alpha = 0.7f),
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )

                }
            }

            FavoriteCellInfo(favoriteArticle, bubbleColor, textColor)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableFavoriteArticleCell(
    favoriteArticle: FavoriteArticle,
    surfaceColor: Color,
    bubbleColor: Color,
    isDark: Boolean,
    deleteAction: () -> Unit,
    onItemClick: () -> Unit
) {
    val swipeSize = (LocalConfiguration.current.screenWidthDp / 3).dp
    val swipePx = with(LocalDensity.current) { swipeSize.toPx() }
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val swipeAnchors = mapOf(0f to 0, -swipePx to 1)
    var boxHeight by remember { mutableStateOf(0) }

    Column {
        Box(
            modifier = Modifier.swipeable(
                swipeableState,
                anchors = swipeAnchors,
                thresholds = { _, _ ->
                    FractionalThreshold(0.3f)
                },
                orientation = Orientation.Horizontal
            )
        ) {
            with(LocalDensity.current) {
                Card(
                    modifier = Modifier
                        .height(boxHeight.toDp())
                        .fillMaxWidth()
                        .alpha(-1 * swipeableState.offset.value / swipePx),
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = surfaceColor,
                ) {
                    Row {
                        Spacer(modifier = Modifier.fillMaxWidth(0.65f))

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(bubbleColor),
                                onClick = { deleteAction() }) {
                                Icon(
                                    modifier = Modifier.size(50.dp),
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Șterge marcaj",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(swipeableState.offset.value.toInt(), 0) }
            ) {
                FavoriteArticleCell(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            boxHeight = layoutCoordinates.size.height
                        },
                    favoriteArticle = favoriteArticle,
                    isDark = isDark,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun FavoriteCellInfo(favoriteArticle: FavoriteArticle, bubbleColor: Color, textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val date =
                    ZonedDateTime.parse(favoriteArticle.timestamp, DateTimeFormatter.ISO_DATE_TIME)
                val duration = Duration.between(date.toInstant(), Instant.now())

                if (favoriteArticle.tags.isEmpty()) {
                    Spacer(modifier = Modifier.weight(7f))
                } else {
                    Row(
                        modifier = Modifier.weight(7f),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        favoriteArticle.tags.forEach { tag ->
                            TagBubble(
                                tag = tag,
                                textColor = textColor,
                                bubbleColor = bubbleColor
                            )
                        }
                    }
                }

                Text(
                    text = fmtDuration(duration),
                    style = MaterialTheme.typography.caption,
                    color = textColor
                )
            }
        }
    }
}