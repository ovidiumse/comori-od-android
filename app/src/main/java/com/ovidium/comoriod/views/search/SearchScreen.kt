package com.ovidium.comoriod.views.search

import SuggestionsView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SearchScreen(navController: NavController) {
    val searchModel: SearchModel = viewModel()
    var query by remember { searchModel.query }
    val autocompleteData by remember { searchModel.autocompleteData }
    val searchData by remember { searchModel.searchData }
    var isSearch by remember { searchModel.isSearch}
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var currentJob by remember { mutableStateOf<Job?>(null) }

    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
    ) {
        SearchBar(
            searchText = query,
            onSearchTextChanged = {
                query = it
                isSearch = false
                currentJob?.cancel()
                currentJob = coroutineScope.async {
                    delay(500L)
                    searchModel.autocomplete()
                }
            }, onClearClick = {
                query = ""
                currentJob?.cancel()
                searchModel.autocompleteData.value = Resource(Status.SUCCESS, null, null)
                keyboardController?.show()
            }, onSearchClick = {
                if (query.isNotEmpty()) {
                    isSearch = true
                    keyboardController?.hide()
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
                            AutocompleteList(hits, navController)
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
                            SearchResultsList(hits, navController)
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {}
                }
            }
        } else {
            SuggestionsView(coroutineScope, keyboardController)
        }
    }
}