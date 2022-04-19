package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.launch


@Composable
fun SearchScreen(navController: NavController) {
    val searchModel: SearchModel = viewModel()
    var query by remember { searchModel.query }
    val autocompleteData by remember { searchModel.autocompleteData }
    val searchData by remember { searchModel.searchData }
    var isSearch by remember { mutableStateOf(false )}
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
    ) {
        SearchBar(
            searchText = query,
            onSearchTextChanged = {
                query = it
                coroutineScope.launch {
                    searchModel.autocomplete()
                }
            }, onClearClick = {
                query = ""
            }, onDoneClick = {
                if (query.isNotEmpty()) {
                    isSearch = true
                    coroutineScope.launch {
                        searchModel.search()
                    }
                }
            })

        if (query.isNotEmpty()) {
            if (!isSearch) {
                when (autocompleteData.status) {
                    Status.SUCCESS -> {
                        autocompleteData.data?.hits?.hits?.let { hits ->
                            AutocompleteList(hits)
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {}
                }
            }
            else {

                when (searchData.status) {
                    Status.SUCCESS -> {
                        searchData.data?.hits?.hits?.let { hits ->
                            SearchResultsList(hits)
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {}
                }
            }
        } else {
            // Show suggestions
        }
    }
}

