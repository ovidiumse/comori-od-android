package com.ovidium.comoriod.views.library.volumes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.data.volumes.VolumesResponse
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.StateHandler
import com.ovidium.comoriod.views.library.BooksFilter
import com.ovidium.comoriod.views.library.BooksGrid
import com.ovidium.comoriod.views.library.VolumesGrid

@Composable
fun VolumesForAuthorScreen(navController: NavController, authorFilter: String, jwtUtils: JWTUtils, signInModel: GoogleSignInModel) {

    val libraryModel: LibraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel))

    Column() {
        StateHandler(navController = navController, responseData = libraryModel.volumesData) { data, isLoading ->
            VolumesGrid(navController = navController, response = data, isLoading = isLoading, isDark = isSystemInDarkTheme(), authorFilter = authorFilter)
        }
    }
}