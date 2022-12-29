package com.ovidium.comoriod.views.library.books

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
                                text = "${realIndex + 1}.  ${item._source.title}",
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.subtitle1,
                                fontWeight = if (currentIndex == index) FontWeight.Bold else FontWeight.Normal,
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
        modifier = Modifier.padding(if (showSearchBar) 1.dp else 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = textColor,
            modifier = Modifier
                .clickable(onClick = { showSearchBar = true })
        )
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
                }
            )
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
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Menu",
            tint = textColor,
            modifier = Modifier
                .clickable(onClick = onExitAction)
        )
    }
}