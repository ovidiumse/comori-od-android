package com.ovidium.comoriod.views.search

import SuggestionsView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.data.search.Hits
import com.ovidium.comoriod.data.titles.TitleHit
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.search.filter.FilterCategory
import kotlinx.coroutines.*


@Composable
fun SearchScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    searchModel: SearchModel = viewModel()
) {
    var query by remember { searchModel.query }
    val autocompleteData by remember { searchModel.autocompleteData }
    val searchData by remember { searchModel.searchData }
    var isSearchPending by remember { searchModel.isSearch }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var currentAutocompleteJob by remember { mutableStateOf<Job?>(null) }
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
                                if (!isSearchPending) {
                                    query = it
                                    isSearchPending = false
                                    currentAutocompleteJob?.cancel()
                                    currentAutocompleteJob = coroutineScope.async {
                                        delay(300L)
                                        searchModel.autocomplete()
                                    }
                                }
                            }, onClearClick = {
                                query = ""
                                currentAutocompleteJob?.cancel()
                                searchModel.autocompleteData.value =
                                    Resource(Status.SUCCESS, null, null)
                                keyboardController?.show()
                            }, onSearchClick = {
                                if (query.isNotEmpty()) {
                                    isSearchPending = true
                                    keyboardController?.hide()
                                    searchModel.search()
                                }
                            })
                    } else {
                        Text(
                            text = "${searchData.data?.hits?.hits?.count()} / ${searchData.data?.hits?.total?.value} rezultate",
                            color = getNamedColor("Link", isDark = isDark)!!
                        )
                    }
                },
                isSearch = isSearchPending,
                onMenuClicked = {
                    launchMenu(coroutineScope, scaffoldState)
                },
                onFilterClicked = {
                    showFilterPopup = true
                }
            )
        }
    ) {
        Column {
            if (query.isNotEmpty()) {
                if (!isSearchPending) {
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
                                SearchResultsList(
                                    hits = hits,
                                    navController = navController,
                                    listState = listState,
                                    searchParams = searchParams,
                                )
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
                searchSource = SearchSource.SEARCH,
                onCheck = { category, item ->
                    if (searchParams[category] != null && (searchParams[category]!!.contains(item))) {
                        searchParams[category]!!.remove(item)
                    } else if (searchParams[category] != null && !searchParams[category]!!.contains(
                            item
                        )
                    ) {
                        searchParams[category]!!.add(item)
                    } else if (searchParams[category] == null) {
                        searchParams[category] = mutableListOf(item)
                    }
                },
                onSaveAction = {
                    showFilterPopup = false
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
                    CoroutineScope(Dispatchers.Main).launch {
                        listState.scrollToItem(0, 0)
                    }
                },
                onExitAction = { showFilterPopup = false })
        }
    }
}