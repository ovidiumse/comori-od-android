package com.ovidium.comoriod.views.search

import SuggestionsView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.article.BibleRefsPopup
import com.ovidium.comoriod.views.search.filter.FilterCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SearchScreen(navController: NavController, searchModel: SearchModel = viewModel()) {
    var query by remember { searchModel.query }
    val autocompleteData by remember { searchModel.autocompleteData }
    val searchData by remember { searchModel.searchData }
    var isSearch by remember { searchModel.isSearch }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var currentJob by remember { mutableStateOf<Job?>(null) }
    var showFilterPopup by remember { mutableStateOf(false) }
    val searchParams = remember { mutableStateMapOf<FilterCategory, MutableList<String>>() }
    var urlParams by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SearchTopBar(
                title = "${searchData.data?.hits?.hits?.count()} / ${searchData.data?.hits?.total?.value} rezultate",
                isSearch = isSearch,
                onMenuClicked = { },
                onFilterClicked = {
                    showFilterPopup = true
                }
            )
        }
    ) {
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
                            searchModel.search(params = "")
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
                } else {
                    when (searchData.status) {
                        Status.SUCCESS -> {
                            searchData.data?.hits?.hits?.let { hits ->
                                SearchResultsList(hits, navController, params = urlParams)
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
    if (showFilterPopup) {
        searchData.data?.aggregations.let { aggregations ->
            SearchFilterPopup(
                aggregations = aggregations,
                onCheck = { category, item ->
                    if (searchParams[category] != null && (searchParams[category]!!.contains(item))) {
                        searchParams[category]!!.remove(item)
                    } else if (searchParams[category] != null && !searchParams[category]!!.contains(
                            item
                        )
                    ) {
                        searchParams[category]!!.add(item)
                    } else if (searchParams[category] == null) {
                        searchParams.put(category, mutableListOf(item))
                    }
                    urlParams = ""
                    searchParams.forEach { entry ->
                        if (entry.value.isNotEmpty()) {
                            urlParams += "&${entry.key.value}=${entry.value.joinToString(",")}"
                        }
                    }
                },
                onSaveAction = {
                    showFilterPopup = false
                    println("URLPARAMS: ${urlParams}")
                    coroutineScope.launch {
                        searchModel.search(params = urlParams)
                    }
                },
                onExitAction = { showFilterPopup = false })
        }
    }
}