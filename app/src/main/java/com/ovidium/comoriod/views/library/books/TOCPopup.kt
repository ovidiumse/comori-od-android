package com.ovidium.comoriod.views.library.books

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.titles.TitleHit
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.normalize
import com.ovidium.comoriod.views.search.SearchBar
import kotlin.text.substring
import kotlin.time.measureTime

@Composable
fun TOCPopup(
    titles: List<TitleHit>,
    currentIndex: Int,
    focusRequester: FocusRequester,
    onSelectAction: (Int) -> Unit,
    onExitAction: () -> Unit
) {

    val listState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }

    val isDark = isSystemInDarkTheme()
    val bgColor = getNamedColor("Background", isDark)
    val headerBarColor = getNamedColor("HeaderBar", isDark)
    val primarySurfaceColor = getNamedColor("PrimarySurface", isDark)
    val secondarySurface = getNamedColor("SecondarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("HeaderText", isDark)
    val mutedTextColor = getNamedColor("MutedText", isDark)

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onExitAction
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        val showNumbers = !titles.any { hit -> hit._source.title.matches(Regex("[0-9]+\\..*"))}

        Box(
            Modifier
                .size(screenWidth, screenHeight)
                .background(
                    bgColor,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column {
                Column(modifier = Modifier.background(headerBarColor)) {
                    TOCTopBar(
                        searchText = searchText,
                        focusRequester = focusRequester,
                        onTextChanged = { fieldValue -> searchText = fieldValue },
                        onClearClick = { searchText = "" },
                        onExitAction = onExitAction
                    )
                }
                LazyColumn(
                    state = listState,
                ) {
                    itemsIndexed(if (searchText.isEmpty()) titles else titles.filter {
                        it._source.title.lowercase().normalize()
                            .contains(searchText.lowercase().normalize())
                    }) { index, item ->
                        var realIndex = titles.indexOf(item)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onSelectAction(realIndex) }
                                .background(if (currentIndex == index) primarySurfaceColor else Color.Transparent)
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = if (showNumbers) "${realIndex + 1}.  ${item._source.title}" else item._source.title,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.body1,
                                fontWeight = if (currentIndex == index) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (currentIndex == index) textColor else mutedTextColor,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .weight(0.9f)
                            )
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_arrow_left_24),
                                contentDescription = "Selected",
                                tint = if (currentIndex == index) textColor else Color.Transparent,
                                modifier = Modifier
                                    .size(35.dp)
                                    .fillMaxWidth()
                                    .weight(0.1f)
                            )
                        }
                        Divider(
                            color = mutedTextColor.copy(alpha = 0.5f),
                            thickness = 0.3.dp
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        listState.animateScrollToItem(currentIndex)
    }

}

@Composable
fun TOCTopBar(
    searchText: String,
    focusRequester: FocusRequester,
    onTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onExitAction: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var showSearchBar by remember { mutableStateOf(false) }
    var searchTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                searchText,
                TextRange(searchText.length)
            )
        )
    }

    val textColor = getNamedColor("Text", isDark)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(54.dp)
    ) {
        IconButton(modifier = Modifier.size(48.dp), onClick = { showSearchBar = true }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = textColor,
            )
        }

        if (showSearchBar) {
            SearchBar(
                searchText = searchTextFieldValue,
                shouldFocus = false,
                focusRequester = focusRequester,
                onClearClick = {
                    showSearchBar = false
                    onClearClick()
                },
                onSearchTextChanged = { newFieldValue ->
                    searchTextFieldValue = newFieldValue
                    onTextChanged(newFieldValue.text)
                },
                modifier = Modifier.weight(0.8f)
            )

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

        } else {
            Text(
                text = "Cuprins",
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        IconButton(modifier = Modifier.size(48.dp), onClick = onExitAction)
        {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = textColor,
                modifier = Modifier
                    .clickable(onClick = onExitAction)
            )
        }
    }
}