package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.views.search.filter.FilterCategory

@Composable
fun SearchResultsList(hits: List<Hit>, navController: NavController, searchParams: SnapshotStateMap<FilterCategory, MutableList<String>>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(hits) { index, hit ->
            if (index == 0)
                Spacer(modifier = Modifier.height(16.dp))

            SearchResultsCell(hit = hit, index, navController, searchParams = searchParams)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


