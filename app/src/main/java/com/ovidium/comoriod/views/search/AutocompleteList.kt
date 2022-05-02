package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
    ) {
        itemsIndexed(hits) { index, hit ->
            if (index == 0)
                Spacer(modifier = Modifier.height(16.dp))

            AutocompleteCell(hit, navController)
        }
    }
}