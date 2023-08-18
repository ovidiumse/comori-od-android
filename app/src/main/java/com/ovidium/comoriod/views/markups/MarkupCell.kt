package com.ovidium.comoriod.views.markups

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ovidium.comoriod.components.TagBubble
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.fmtDuration
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MarkupCell(
    modifier: Modifier = Modifier,
    textSelection: String,
    index: Int,
    length: Int,
    markupColor: String,
    timestamp: String?,
    author: String,
    title: String,
    articleID: String,
    book: String,
    tags: List<String>,
    isDark: Boolean,
    onItemClick: () -> Unit
) {
    val initialMaxLines = 4
    val initialText = textSelection.trim()
    var expandedState by remember { mutableStateOf(false) }
    var maxLinesState by remember { mutableStateOf(initialMaxLines) }
    var textOverflowingState by remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf(initialText) }
    var expandedTextHeight by remember { mutableStateOf(0.dp) }
    val localDensity = LocalDensity.current

    val containerColor = getNamedColor("Container", isDark)
    val primarySurfaceColor = getNamedColor("PrimarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("HeaderText", isDark)

    if (initialText != textState)
        textState = textSelection.trim()

    val rotationDegreeState by animateFloatAsState(targetValue = if (expandedState) 180f else 0f)

    val context = LocalContext.current

    fun showSharingSheet() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val sharingData = "“" + textSelection + "”" + "\n" + "https://comori-od.ro/article/${articleID}?index=${index}&length=${length}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharingData)
        ContextCompat.startActivity(context, Intent.createChooser(shareIntent, null), null)
    }


    Card(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = containerColor,
        onClick = onItemClick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                ) {
                    Divider(
                        modifier = Modifier
                            .height(expandedTextHeight)
                            .width(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = getNamedColor(markupColor, isDark)
                    )
                }
                Text(
                    text = "„$textState”",
                    color = textColor,
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
                    },
                    modifier = Modifier
                        .weight(12.4f)
                        .onSizeChanged { size ->
                            expandedTextHeight = with(localDensity) { size.height.toDp() }
                        }
                )
                if (textOverflowingState) {
                    IconButton(
                        modifier = Modifier
                            .weight(0.8f)
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
                            tint = textColor,
                        )
                    }
                }
            }
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(7f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    tags.forEach { tag ->
                        TagBubble(
                            tag = tag,
                            textColor = textColor,
                            bubbleColor = bubbleColor
                        )
                    }
                }

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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(primarySurfaceColor)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    Text(
                        text = "${author} - ${title}",
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.caption,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book,
                        color = textColor,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.caption
                    )
                }
                IconButton(
                    onClick = { showSharingSheet() },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "Share markup",
                        tint = textColor
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableMarkupCell(
    markup: Markup,
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
                MarkupCell(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            boxHeight = layoutCoordinates.size.height
                        },
                    textSelection = markup.selection,
                    index = markup.index,
                    length = markup.length,
                    markupColor = markup.bgColor,
                    timestamp = markup.timestamp,
                    title = markup.title,
                    author = markup.author,
                    articleID = markup.articleID,
                    book = markup.book,
                    tags = markup.tags,
                    isDark = isDark,
                    onItemClick = onItemClick
                )
            }
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

    SwipeableMarkupCell(
        markup = markup,
        isDark = isDark,
        surfaceColor = getNamedColor("PrimarySurface", isDark),
        bubbleColor = getNamedColor("Bubble", isDark),
        deleteAction = { }) {
    }
}

