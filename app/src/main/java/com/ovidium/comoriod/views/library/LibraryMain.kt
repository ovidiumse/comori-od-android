package com.ovidium.comoriod.views.library

import androidx.compose.animation.core.tween
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
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.library.bible.BibleBanner

@Composable
fun LibraryMain(
    navController: NavController,
    signInModel: GoogleSignInModel,
    libraryModel: LibraryModel,
    isDark: Boolean,
    showAuthorAction: (Bucket?) -> Unit
) {
    if (libraryModel.recommendedData.value.status == Status.UNINITIALIZED && signInModel.userResource.value.state == UserState.LoggedIn)
        libraryModel.loadRecommended()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = getNamedColor("Background", isDark))
    ) {
        item {
            StateHandler(navController, libraryModel.authorsData) { data, isLoading ->
                BibleBanner(data, navController)
            }
        }
        item {
            StateHandler(
                navController,
                libraryModel.recentlyAddedBooksData
            ) { data, isLoading ->
                RecentlyAddedBooksRow(
                    navController, data, isLoading, isDark, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
        }

        if (signInModel.userResource.value.state == UserState.LoggedIn) {
            item {
                RecommendedRow(
                    navController, libraryModel.recommendedData, isDark, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
            item {
                StateHandler(navController, libraryModel.trendingData) { data, isLoading ->
                    TrendingRow(
                        navController, data, isLoading, isDark, modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(durationMillis = 300)
                        )
                    )
                }
            }
        }
        item {
            StateHandler(navController, libraryModel.authorsData) { data, isLoading ->
                AuthorsRow(
                    navController, data, isLoading, isDark, showAuthorAction, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
        }
        item {
            StateHandler(navController, libraryModel.volumesData) { data, isLoading ->
                VolumesRow(
                    navController, data, isLoading, isDark, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
        }
    }
}