package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.model.SearchModel

@Composable
fun SearchResultsList(
    hits: List<Hit>,
    navController: NavController,
    listState: LazyListState,
    isDark: Boolean,
    loadMoreAction: (offset: Int) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(hits) { index, hit ->
            SearchResultsCell(index, hit = hit, navController = navController, isDark)
            Spacer(modifier = Modifier.height(12.dp))

            if (index == hits.lastIndex)
                loadMoreAction(hits.count())
        }
    }
}





