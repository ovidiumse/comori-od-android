package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.data.autocomplete.Hit
import com.ovidium.comoriod.views.search.AutocompleteCell


@Composable
fun AutocompleteList(hits: List<Hit>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
            .padding(bottom = 56.dp)
            .padding(top = 16.dp)
    ) {
        items(hits) { hit ->
            AutocompleteCell(hit)
        }
    }
}