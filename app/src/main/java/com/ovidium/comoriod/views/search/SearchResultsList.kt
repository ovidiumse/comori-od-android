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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.library.authors.TitlesForAuthorCell
import com.ovidium.comoriod.views.search.filter.FilterCategory

@Composable
fun SearchResultsList(
    hits: List<Hit>,
    navController: NavController,
    listState: LazyListState,
    searchParams: SnapshotStateMap<FilterCategory, MutableList<String>>?,
    isDark: Boolean
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(hits) { index, hit ->
            if (index == 0)
                Spacer(modifier = Modifier.height(16.dp))

            SearchResultsCell(
                hit = hit,
                index = index,
                navController = navController,
                searchParams = searchParams,
                isDark,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}





