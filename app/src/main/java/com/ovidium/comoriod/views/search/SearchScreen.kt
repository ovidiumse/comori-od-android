package com.ovidium.comoriod.views.search

import SuggestionsView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
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
    val scaffoldState = rememberScaffoldState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var currentJob by remember { mutableStateOf<Job?>(null) }
    var showFilterPopup by remember { mutableStateOf(false) }
    val searchParams = remember { mutableStateMapOf<FilterCategory, MutableList<String>>() }

    Scaffold(
        topBar = {
            val isDark = isSystemInDarkTheme()
            SearchTopBar(
                title = {
                    if (searchData.data?.hits?.hits.isNullOrEmpty()) {
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
                    } else {
                        Text(
                            text = "${searchData.data?.hits?.hits?.count()} / ${searchData.data?.hits?.total?.value} rezultate",
                            color = getNamedColor("Link", isDark = isDark)!!
                        )
                    }
                },
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
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
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
                                SearchResultsList(hits, navController, searchParams = searchParams)
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
                },
                onSaveAction = {
                    showFilterPopup = false
                    coroutineScope.launch {
                        val types =
                            if (searchParams[FilterCategory.TYPES].isNullOrEmpty()) "" else searchParams[FilterCategory.TYPES]!!.joinToString(
                                ","
                            )
                        val authors =
                            if (searchParams[FilterCategory.AUTHORS].isNullOrEmpty()) "" else searchParams[FilterCategory.AUTHORS]!!.joinToString(
                                ","
                            )
                        val volumes =
                            if (searchParams[FilterCategory.VOLUMES].isNullOrEmpty()) "" else searchParams[FilterCategory.VOLUMES]!!.joinToString(
                                ","
                            )
                        val books =
                            if (searchParams[FilterCategory.BOOKS].isNullOrEmpty()) "" else searchParams[FilterCategory.BOOKS]!!.joinToString(
                                ","
                            )
                        searchModel.search(
                            type = types,
                            authors = authors,
                            volumes = volumes,
                            books = books
                        )
                    }
                },
                onExitAction = { showFilterPopup = false })
        }
    }
}