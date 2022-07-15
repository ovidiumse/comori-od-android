package com.ovidium.comoriod.views.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun LibraryMain(
    navController: NavController,
    signInModel: GoogleSignInModel,
    libraryModel: LibraryModel,
    isDark: Boolean,
    showAuthorAction: (Bucket?) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = getNamedColor("Background", isDark))
    ) {
        if (signInModel.userResource.value.state == UserState.LoggedIn) {
            item {
                StateHandler(
                    navController,
                    libraryModel.recentlyAddedBooksData
                ) { data, isLoading ->
                    RecentlyAddedBooksRow(navController, data, isLoading, isDark)
                }
            }
            item {
                StateHandler(navController, libraryModel.recommendedData) { data, isLoading ->
                    RecommendedRow(navController, data, isLoading, isDark)
                }
            }
            item {
                StateHandler(navController, libraryModel.trendingData) { data, isLoading ->
                    TrendingRow(navController, data, isLoading, isDark)
                }
            }
        }
        item {
            StateHandler(navController, libraryModel.authorsData) { data, isLoading ->
                AuthorsRow(navController, data, isLoading, isDark, showAuthorAction)
            }
        }
        item {
            StateHandler(navController, libraryModel.volumesData) { data, isLoading ->
                VolumesRow(navController, data, isLoading, isDark)
            }
        }
    }
}