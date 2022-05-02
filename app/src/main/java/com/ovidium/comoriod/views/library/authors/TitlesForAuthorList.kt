package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.ovidium.comoriod.views.search.filter.FilterCategory

@Composable
fun TitlesForAuthorList(
    navController: NavController,
    jwtUtils: JWTUtils,
    signInModel: GoogleSignInModel,
    libraryModel: LibraryModel,
    searchParams: SnapshotStateMap<FilterCategory, MutableList<String>>?,
) {

    val titlesForAuthorData by remember { libraryModel.titlesForAuthorData }
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
    ) {
        val hits = titlesForAuthorData.data?.hits?.hits.let { it } ?: return@LazyColumn
        itemsIndexed(hits) { index, hit ->
            if (index == 0)
                Spacer(modifier = Modifier.height(16.dp))

            TitlesForAuthorCell(
                hit = hit,
                index = index,
                navController = navController,
                searchParams = searchParams,
                jwtUtils = jwtUtils,
                signInModel = signInModel,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}