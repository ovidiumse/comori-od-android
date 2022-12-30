package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.model.SearchModel

@Composable
fun SearchResultsList(
    searchModel: SearchModel,
    query: String,
    hits: List<Hit>,
    navController: NavController,
    listState: LazyListState,
    params: Map<String, String>,
    isDark: Boolean
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(hits) { index, hit ->
            if (index == 0)
                Spacer(modifier = Modifier.height(12.dp))

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





