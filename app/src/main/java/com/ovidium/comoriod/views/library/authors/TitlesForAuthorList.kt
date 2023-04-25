package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Status

@Composable
fun TitlesForAuthorList(
    navController: NavController,
    libraryModel: LibraryModel,
    params: Map<String, String>
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = getNamedColor("Container", isDark)
    val mutedTextColor = getNamedColor("MutedText", isDark)
    val bubbleColor = getNamedColor("PrimarySurface", isDark)

    val titlesData by libraryModel.titlesData
    val titles = titlesData.data?.titles

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        when (titlesData.status) {
            Status.SUCCESS -> {
                if (!titles.isNullOrEmpty()) {
                    titles.forEachIndexed { index, titleHit ->
                        item {
                            if (index == 0)
                                Spacer(modifier = Modifier.height(12.dp))

                            TitlesForAuthorCell(
                                hit = titleHit,
                                index = index,
                                navController = navController,
                                bgColor = bgColor,
                                mutedTextColor = mutedTextColor,
                                bubbleColor = bubbleColor
                            )

                            if (index == titles.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))

                                libraryModel.getTitles(offset = titles.count(), params = params)
                            }
                        }
                    }
                }
            }
            Status.ERROR -> {}
            Status.LOADING -> item { Text("Loading...") }
        }
    }
}