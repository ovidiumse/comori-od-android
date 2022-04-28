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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.data.search.Hit

@Composable
fun SearchResultsList(hits: List<Hit>, navController: NavController, params: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
            .padding(bottom = 56.dp)
    ) {
        itemsIndexed(hits) { index, hit ->
            SearchResultsCell(hit = hit, index, navController, params = params)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


