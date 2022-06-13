package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Status

@Composable
fun TitlesForAuthorList(
    navController: NavController,
    libraryModel: LibraryModel,
    params: Map<String, String>
) {
    val titlesData by libraryModel.titlesData
    val titles = titlesData.data?.titles

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
    ) {
        when (titlesData.status) {
            Status.SUCCESS -> {
                if (!titles.isNullOrEmpty()) {
                    titles.forEachIndexed { index, titleHit ->
                        item {
                            if (index == 0)
                                Spacer(modifier = Modifier.height(16.dp))

                            TitlesForAuthorCell(
                                hit = titleHit,
                                index = index,
                                navController = navController,
                                libraryModel = libraryModel
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            if (index == titles.lastIndex)
                                libraryModel.getTitles(offset = titles.count(), params = params)
                        }
                    }
                }
            }
            Status.ERROR -> TODO()
            Status.LOADING -> item { Text("Loading...") }
        }
    }
}