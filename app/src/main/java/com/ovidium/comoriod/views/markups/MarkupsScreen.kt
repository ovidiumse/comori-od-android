package com.ovidium.comoriod.views.markups

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.components.NoContentPlaceholder
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.TagsRow
import com.ovidium.comoriod.views.favorites.DeleteFavoriteConfirmationDialog
import java.net.URLEncoder
import kotlin.math.sign

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MarkupsScreen(
    navController: NavController,
    markupsModel: MarkupsModel,
    signInModel: GoogleSignInModel,
    scaffoldState: ScaffoldState
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = getNamedColor("Background", isDark)
    val surfaceColor = getNamedColor("PrimarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)

    val markupsData = markupsModel.markups

    if (markupsData.value.status == Status.UNINITIALIZED && signInModel.userResource.value.state == UserState.LoggedIn)
        markupsModel.loadMarkups()

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
            AppBar(
                title = "Pasaje",
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            markups.forEachIndexed { index, markup ->
                                item(key = markup.id) {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp)
                                            .animateItemPlacement(
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
                                                        }?markupId=${URLEncoder.encode(markup.id, "utf-8")}"
                                                    )
                                                )
                                            })
                                    }

                                    if (index == markups.size - 1)
                                        Spacer(modifier = Modifier.height(12.dp))
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