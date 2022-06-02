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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun TOCPopup(
    titles: List<TitleHit>,
    currentIndex: Int,
    focusRequester: FocusRequester,
    onSelectAction: (Int) -> Unit,
    onExitAction: () -> Unit
) {

    val listState = rememberLazyListState()
    val searchText = remember { mutableStateOf("") }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onExitAction
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        val isDark = isSystemInDarkTheme()
        Box(
            Modifier
                .size(screenWidth, screenHeight)
                .background(
                    getNamedColor("Container", isDark = isDark),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column {
                TOCTopBar(
                    searchText = searchText,
                    focusRequester = focusRequester,
                    onExitAction = onExitAction
                )
                LazyColumn(
                    state = listState,
                    modifier = Modifier.padding(16.dp)
                ) {
                    itemsIndexed(if (searchText.value.isEmpty()) titles else titles.filter {
                        it._source.title.lowercase().normalize()
                            .contains(searchText.value.lowercase().normalize())
                    }) { index, item ->
                        var realIndex = titles.indexOf(item)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onSelectAction(realIndex) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = "${realIndex + 1}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (currentIndex == index) getNamedColor(
                                    "Link",
                                    isDark = isDark
                                ) else getNamedColor("PopupContainer", isDark = isDark),
                                modifier = Modifier
                                    .padding(end = 16.dp)
                            )
                            Text(
                                text = item._source.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (currentIndex == index) getNamedColor(
                                    "Link",
                                    isDark = isDark
                                ) else getNamedColor("PopupContainer", isDark = isDark),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.7f)
                            )
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_arrow_left_24),
                                contentDescription = "Selected",
                                tint = if (currentIndex == index) getNamedColor(
                                    "Link",
                                    isDark = isDark
                                ) else Color.Transparent,
                                modifier = Modifier
                                    .size(35.dp)
                                    .fillMaxWidth()
                                    .weight(0.1f)
                            )
                        }
                        Divider(
                            color = if (isDark) Color.DarkGray else Color.LightGray,
                            thickness = 0.7.dp
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
    searchText: MutableState<String>,
    focusRequester: FocusRequester,
    onExitAction: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var showSearchBar by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp, top = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = getNamedColor("Link", isDark = isDark),
            modifier = Modifier
                .clickable(onClick = { showSearchBar = true })
        )
        if (showSearchBar) {
            SearchBar(
                searchText = searchText.value,
                focusRequester = focusRequester,
                onClearClick = {
                    showSearchBar = false
                    searchText.value = ""
                },
                onSearchTextChanged = { searchText.value = it }
            )
        } else {
            Text(
                text = "Cuprins",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp)
            )
        }
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Menu",
            tint = getNamedColor("Link", isDark = isDark),
            modifier = Modifier
                .clickable(onClick = onExitAction)
        )
    }
}