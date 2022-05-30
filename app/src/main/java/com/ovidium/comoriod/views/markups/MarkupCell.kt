package com.ovidium.comoriod.views.markups

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.TagBubble
import com.ovidium.comoriod.components.TextCard
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.ParagraphStyle
import com.ovidium.comoriod.utils.articulate
import com.ovidium.comoriod.views.Screens
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MarkupCell(
    markup: Markup,
    isDark: Boolean,
    deleteAction: (String) -> Unit,
    onItemClick: () -> Unit
) {
    val initialMaxLines = 4
    val initialText = markup.selection.trim()
    var expandedState by remember { mutableStateOf(false) }
    var maxLinesState by remember { mutableStateOf(initialMaxLines) }
    var textOverflowingState by remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf(initialText) }

    if (initialText != textState)
        textState = markup.selection.trim()

    val rotationDegreeState by animateFloatAsState(targetValue = if (expandedState) 180f else 0f)

    Card(
        modifier = Modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        ),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = getNamedColor(markup.bgColor, isDark),
        onClick = onItemClick
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
            ) {
                Text(
                    modifier = Modifier.weight(12f),
                    text = "„$textState”",
                    color = Color.Black.copy(alpha = .9f),
                    letterSpacing = 0.3.sp,
                    maxLines = maxLinesState,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                            textOverflowingState = true

                            val end =
                                textLayoutResult.getLineEnd(textLayoutResult.lineCount - 1, true)
                            val wordRange = textLayoutResult.getWordBoundary(end - 3)
                            textState = initialText.substring(0, wordRange.start - 2) + "..."
                        }
                    }
                )
                if (textOverflowingState) {
                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .then(Modifier.size(24.dp))
                            .alpha(ContentAlpha.medium),
                        onClick = {
                            expandedState = !expandedState
                            if (expandedState) {
                                textState = initialText
                                maxLinesState = Int.MAX_VALUE
                            } else
                                maxLinesState = initialMaxLines
                        }) {
                        Icon(
                            modifier = Modifier.rotate(rotationDegreeState),
                            imageVector = Icons.Rounded.ArrowDropDown,
                            contentDescription = "Expand Arrow",
                        )
                    }
                }
            }
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                val date = ZonedDateTime.parse(markup.timestamp, DateTimeFormatter.ISO_DATE_TIME)
                val duration = Duration.between(date.toInstant(), Instant.now())

                fun fmtDuration(duration: Duration): String {
                    val durations = listOf(
                        Pair(duration.toDays(), Pair("zile", "zi")),
                        Pair(duration.toHours(), Pair("ore", "oră")),
                        Pair(duration.toMinutes(), Pair("minute", "minut"))
                    )

                    var threshold = 0
                    for ((cnt, units) in durations) {
                        val (multiple, single) = units
                        if (cnt > threshold)
                            return "acum ${articulate(cnt.toInt(), multiple, single)}"
                        else
                            threshold = 1
                    }

                    return "acum"
                }

                LazyRow(
                    modifier = Modifier.weight(7f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    markup.tags.forEach { tag ->
                        item() {
                            TagBubble(
                                tag = tag,
                                textColor = Color.Black.copy(alpha = .8f),
                                bubbleColor = Color.White.copy(alpha = .5f)
                            )
                        }
                    }
                }

                Text(
                    text = fmtDuration(duration),
                    style = MaterialTheme.typography.caption,
                    color = Color.Black.copy(alpha = .7f)
                )
            }
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = .5f))
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                    Text(
                        text = "${markup.author} - ${markup.title}",
                        color = Color.White.copy(alpha = .9f),
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        text = markup.book,
                        color = Color.White.copy(alpha = .9f),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}

@Composable
fun MarkupCell3(
    markup: Markup,
    textColor: Color,
    bottomBarColor: Color,
    detailTextColor: Color,
    mutedTextColor: Color,
    typeBubbleColor: Color,
    isDark: Boolean,
    deleteAction: (String) -> Unit,
    onClickItem: () -> Unit,
) {
    Card(shape = RoundedCornerShape(10.dp)) {
        Column() {
            Row {
                Text(text = markup.title)
                Button(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = "Expand Arrow"
                    )
                }
            }
            Row(
                modifier = Modifier
                    .background(getNamedColor(markup.bgColor, isDark))
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = "„" + markup.selection.trim() + "”",
                    color = textColor,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .background(bottomBarColor)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 5.dp, horizontal = 12.dp)) {
                    Row {
                        Icon(
                            modifier = Modifier.size(14.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_author),
                            contentDescription = "Author icon",
                            tint = detailTextColor
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = markup.author + ", " + markup.title,
                            color = detailTextColor,
                            style = MaterialTheme.typography.caption
                        )
                    }
                    Row {
                        Icon(
                            modifier = Modifier.size(14.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
                            contentDescription = "Book icon",
                            tint = mutedTextColor
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = markup.book,
                            color = mutedTextColor,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarkupCell2(
    navController: NavController,
    markup: Markup,
    deleteAction: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = getNamedColor(markup.bgColor, isSystemInDarkTheme()),
        elevation = 1.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .clickable { navController.navigate(Screens.Article.withArgs("${markup.articleID}?scrollOffset=${markup.scrollOffset}")) }
        ) {
            Text(
                text = markup.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            MarkupCellTitle(markup)
            MarkupCellInfo(markup, deleteAction)
        }
    }
}

@Composable
@Preview
fun MarkupCellPreview() {
    val isDark = isSystemInDarkTheme()

    val markup = Markup(
        "dummy_id",
        index = 160,
        length = 74,
        title = "Constiinta misiunii noastre",
        author = "Traian Dorz",
        type = "articol",
        book = "Semanati Cuvantul Sfant",
        selection = "Conştiinţa colectivităţii noastre evanghelice trebuie să determine toată orientarea şi activitatea noastră. Să depăşim - cum s-ar zice stadiul egoist al proprietăţii şi intereselor, iubind, trecând la nivelul înalt, altruist şi superior al părtăşiei, al intereselor şi proprietăţii noastre frăţeşti, în specificul divin al marii Evanghelii în care ne-a încadrat Hristos, Domnul nostru.",
        bgColor = "markupSkye",
        timestamp = "2022-05-22T15:07:57.702802Z",
        tags = listOf("dimineata", "test", "suferinta")
    )

    MarkupCell(
        markup = markup,
        isDark = isDark,
        deleteAction = { }) {
    }
}


@Composable
fun MarkupCellTitle(markup: Markup) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
                contentDescription = "Menu",
                tint = Color.Black,
                modifier = Modifier
                    .size(16.dp)
            )
            Text(
                text = markup.book,
                style = MaterialTheme.typography.caption,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
        Text(
            text = markup.selection,
            style = MaterialTheme.typography.body1,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 8.dp)
        )
    }
}


@Composable
fun MarkupCellInfo(markup: Markup, deleteAction: (String) -> Unit) {
    Column(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.Start) {

                if (markup.tags.isNotEmpty()) {
                    println("TAGS: ${markup.tags}, isEmpty: ${markup.tags.isEmpty()}, count: ${markup.tags.count()}")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_tag_24),
                            contentDescription = "Tag",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(end = 5.dp)
                        )

                        markup.tags.forEach { tag ->
                            if (tag.isNotEmpty())
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.caption,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .background(Color.Red, RoundedCornerShape(50))
                                        .padding(5.dp)
                                )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_access_time_24),
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 5.dp)
                    )
                    var inFormatter = DateTimeFormatter.ISO_DATE_TIME
                    val rawDate = LocalDate.parse(markup.timestamp, inFormatter)
                    val formattedDate = "${rawDate.dayOfMonth} ${rawDate.month} - ${rawDate.year}"
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.caption,
                        color = Color.White
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
                        .clickable { deleteAction(markup.id) }
                )
            }
        }
    }
}