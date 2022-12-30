package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.ovidium.comoriod.model.LibraryModel

@Composable
fun TitlesForAuthorScreen(
    navController: NavController,
    libraryModel: LibraryModel,
    params: Map<String, String>
) {

    Column {
        TitlesForAuthorList(
            libraryModel = libraryModel,
            navController = navController,
            params = params
        )
    }
}