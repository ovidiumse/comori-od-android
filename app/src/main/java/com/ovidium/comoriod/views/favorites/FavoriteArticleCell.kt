package com.ovidium.comoriod.views.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.AdaptiveText
import com.ovidium.comoriod.components.TagBubble
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.fmtDuration
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.markups.MarkupCell
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun FavoriteArticleCell(
    modifier: Modifier = Modifier,
    title: String,
    author: String,
    book: String,
    timestamp: String?,
    tags: List<String>,
    isDark: Boolean,
    onItemClick: () -> Unit,
) {
    val containerColor = getNamedColor("Container", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("HeaderText", isDark)
    val mutedTextColor = getNamedColor("MutedText", isDark)

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
                AdaptiveText(
                    text = title,
                    minFontSize = 11.2.sp,
                    maxFontSize = 22.sp,
                    style = MaterialTheme.typography.body1,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
                        contentDescription = "Book icon",
                        tint = mutedTextColor
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    AdaptiveText(
                        text = book,
                        minFontSize = 8.sp,
                        maxFontSize = 16.sp,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = mutedTextColor
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_author),
                        contentDescription = "Author icon",
                        tint = mutedTextColor
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    AdaptiveText(
                        text = author,
                        minFontSize = 8.sp,
                        maxFontSize = 16.sp,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = mutedTextColor
                    )
                }
            }

            FavoriteCellInfo(timestamp, tags, bubbleColor, textColor)
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
                    title = favoriteArticle.title,
                    author = favoriteArticle.author,
                    book = favoriteArticle.book,
                    timestamp = favoriteArticle.timestamp,
                    tags = favoriteArticle.tags,
                    isDark = isDark,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun FavoriteCellInfo(timestamp: String?, tags: List<String>, bubbleColor: Color, textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {


                if (tags.isEmpty()) {
                    Spacer(modifier = Modifier.weight(7f))
                } else {
                    LazyRow(
                        modifier = Modifier.weight(7f),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(tags.size) { idx ->
                            val tag = tags[idx]
                            TagBubble(
                                tag = tag,
                                textColor = textColor,
                                bubbleColor = bubbleColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (!timestamp.isNullOrEmpty()) {
                    val date = ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
                    val duration = Duration.between(date.toInstant(), Instant.now())

                    Text(
                        text = fmtDuration(duration),
                        style = MaterialTheme.typography.caption,
                        color = textColor
                    )
                }
            }
        }
    }
}