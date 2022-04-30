package com.ovidium.comoriod.views.library

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.views.*

@Composable
fun LibraryMain(signInModel: GoogleSignInModel, libraryModel: LibraryModel, isDark: Boolean) {

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        if (signInModel.userResource.value.state == UserState.LoggedIn) {
            item {
                StateHandler(libraryModel.recentlyAddedBooksData) { data, isLoading ->
                    RecentlyAddedBooksRow(data, isLoading, isDark)
                }
            }
            item {
                StateHandler(libraryModel.recommendedData) { data, isLoading ->
                    RecommendedRow(data, isLoading, isDark)
                }
            }
            item {
                StateHandler(libraryModel.trendingData) { data, isLoading ->
                    TrendingRow(data, isLoading, isDark)
                }
            }
        }
        item {
            StateHandler(libraryModel.authorsData) { data, isLoading ->
                AuthorsRow(data, isLoading, isDark)
            }
        }
        item {
            StateHandler(libraryModel.volumesData) { data, isLoading ->
                VolumesRow(data, isLoading, isDark)
            }
        }
    }
}