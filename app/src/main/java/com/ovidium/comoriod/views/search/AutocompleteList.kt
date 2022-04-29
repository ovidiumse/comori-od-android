package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.data.autocomplete.Hit
import com.ovidium.comoriod.views.search.AutocompleteCell


@Composable
fun AutocompleteList(hits: List<Hit>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        items(hits) { hit ->
            AutocompleteCell(hit, navController)
        }
    }
}