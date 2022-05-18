package com.ovidium.comoriod.views.markups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.TagsRow
import com.ovidium.comoriod.views.favorites.DeleteFavoriteConfirmationDialog

@Composable
fun MarkupsScreen(
    navController: NavController,
    markupsModel: MarkupsModel,
    scaffoldState: ScaffoldState
) {
    val markupsData = markupsModel.markups
    val tags = markupsData.value.data?.map { markup -> markup.tags }?.flatten()?.distinct()
        ?.filter { tag -> tag.isNotEmpty() }
        ?: emptyList()

    val markupToDelete = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var selectedTag by remember { mutableStateOf("") }

    fun getMarkups(): List<Markup>? {
        if (!tags.contains(selectedTag))
            selectedTag = ""

        return if (selectedTag.isEmpty())
            markupsData.value.data
        else
            markupsData.value.data?.filter { mark -> mark.tags.contains(selectedTag) }
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
                .background(MaterialTheme.colors.background)
        ) {
            when (markupsData.value.status) {
                Status.SUCCESS -> {
                    val markups = getMarkups()

                    TagsRow(
                        tags,
                        selectedTag,
                        onTagsChanged = { tag -> selectedTag = tag })

                    LazyColumn() {
                        markups?.forEach { markup ->
                            item() {
                                MarkupCell(navController, markup) { idToDelete ->
                                    markupToDelete.value = idToDelete
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }

        if (markupToDelete.value.isNotEmpty()) {
            DeleteFavoriteConfirmationDialog(
                deleteAction = {
                    markupsModel.deleteMarkup(markupToDelete.value)
                    markupToDelete.value = ""
                },
                dismissAction = { markupToDelete.value = "" }
            )
        }
    }
}