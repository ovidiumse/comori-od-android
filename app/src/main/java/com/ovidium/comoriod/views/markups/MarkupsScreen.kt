package com.ovidium.comoriod.views.markups

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.components.NoContentPlaceholder
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.TagsRow
import com.ovidium.comoriod.views.favorites.DeleteFavoriteConfirmationDialog
import java.net.URLEncoder

@Composable
fun MarkupsScreen(
    navController: NavController,
    markupsModel: MarkupsModel,
    scaffoldState: ScaffoldState
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = getNamedColor("Background", isDark)
    val surfaceColor = getNamedColor("PrimarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)

    val markupsData = markupsModel.markups
    val tags =
        markupsData.value.data?.reversed()?.map { markup -> markup.tags }?.flatten()?.distinct()
            ?.filter { tag -> tag.isNotEmpty() }
            ?: emptyList()

    val coroutineScope = rememberCoroutineScope()
    var selectedTag by remember { mutableStateOf("") }

    fun getMarkups(): List<Markup>? {
        if (!tags.contains(selectedTag))
            selectedTag = ""

        return if (selectedTag.isEmpty())
            markupsData.value.data?.reversed()
        else
            markupsData.value.data?.reversed()?.filter { mark -> mark.tags.contains(selectedTag) }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                title = { Text(text = "Pasaje") },
                isSearch = false,
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState) }) {
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {
            when (markupsData.value.status) {
                Status.SUCCESS -> {
                    val markups = getMarkups()

                    if (markups == null || markups.isEmpty())
                        NoContentPlaceholder("Nu ai nici un pasaj favorit")
                    else {
                        TagsRow(
                            tags,
                            selectedTag,
                            onTagsChanged = { tag -> selectedTag = tag })

                        LazyColumn(
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            markups.forEach { markup ->
                                item(key = markup.id) {
                                    Column(
                                        modifier = Modifier.animateItemPlacement(
                                            animationSpec = tween(durationMillis = 300)
                                        )
                                    ) {
                                        SwipeableMarkupCell(
                                            markup = markup,
                                            surfaceColor = surfaceColor,
                                            bubbleColor = bubbleColor,
                                            isDark = isDark,
                                            deleteAction = {
                                                markupsModel.deleteMarkup(markup.id)
                                            },
                                            onItemClick = {
                                                navController.navigate(
                                                    Screens.Article.withArgs(
                                                        "${
                                                            URLEncoder.encode(
                                                                markup.articleID,
                                                                "utf-8"
                                                            )
                                                        }?markupId=${markup.id}"
                                                    )
                                                )
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}