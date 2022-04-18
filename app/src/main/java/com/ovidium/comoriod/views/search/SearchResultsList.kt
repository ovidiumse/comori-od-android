package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.data.search.Hit

@Composable
fun SearchResultsList(hits: List<Hit>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
            .padding(bottom = 56.dp)
            .padding(top = 16.dp)
    ) {
        itemsIndexed(hits) { index, hit ->
            SearchResultsCell(hit = hit, index)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}