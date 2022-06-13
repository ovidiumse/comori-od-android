package com.ovidium.comoriod.views.search

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.library.authors.TitlesForAuthorCell

@Composable
fun SearchResultsList(
    query: String,
    hits: List<Hit>,
    navController: NavController,
    listState: LazyListState,
    params: Map<String, String>,
    isDark: Boolean
) {
    val searchModel: SearchModel = viewModel()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(hits) { index, hit ->
            SearchResultsCell(index, hit = hit, navController = navController, isDark)
            Spacer(modifier = Modifier.height(12.dp))

            if (index == hits.lastIndex)
                searchModel.search(
                    query,
                    offset = hits.count(),
                    params = params
                )
        }
    }
}





