package com.ovidium.comoriod.views.search

import SuggestionsView
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.views.search.filter.SearchFilterPopup
import kotlinx.coroutines.*


@Composable
fun SearchScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    searchModel: SearchModel
) {
    var query by remember { searchModel.query }
    var searchTextFieldValue by remember { mutableStateOf(TextFieldValue(query, TextRange(query.length))) }
    val autocompleteData = searchModel.autocompleteData
    val searchData = searchModel.searchData
    val aggregations = searchModel.aggregations
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var currentAutocompleteJob by remember { mutableStateOf<Job?>(null) }
    var showFilterPopup by remember { mutableStateOf(false) }
    val searchParams = remember { mutableStateMapOf<String, MutableSet<String>>() }
    val focusRequester = remember { FocusRequester() }

    val isDark = isSystemInDarkTheme()
    val primarySurfaceColor = getNamedColor("PrimarySurface", isDark)
    val backgroundColor = getNamedColor("Background", isDark)
    val textColor = getNamedColor("Text", isDark)
    val headerTextColor = getNamedColor("HeaderText", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)

    fun getParams(): Map<String, String> {
        return searchParams.map { entry -> Pair(entry.key, entry.value.joinToString()) }
            .associate { pair -> pair }
    }

    Scaffold(
        topBar = {
            AppBar(
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState) },
                actions = @Composable {
                    SearchBar(
                        searchText = searchTextFieldValue,
                        focusRequester = focusRequester,
                        onSearchTextChanged = { newFieldValue ->
                            if (searchData.value.status != Status.LOADING) {
                                query = newFieldValue.text
                                searchTextFieldValue = newFieldValue

                                if (searchData.value.status == Status.SUCCESS)
                                    searchData.value = Resource.uninitialized()

                                currentAutocompleteJob?.cancel()
                                currentAutocompleteJob = coroutineScope.async {
                                    delay(300L)
                                    searchModel.autocomplete(query)
                                }
                            }
                        },
                        onSearchClick = {
                            if (query.isNotEmpty()) {
                                keyboardController?.hide()
                                searchModel.search(query)
                                searchParams.clear()
                            }
                        },
                        onClearClick = {
                            query = ""
                            searchTextFieldValue = TextFieldValue(query, TextRange(query.length))
                            
                            currentAutocompleteJob?.cancel()
                            autocompleteData.value = Resource.uninitialized()
                            searchData.value = Resource.uninitialized()
                            keyboardController?.show()
                        })
                })
        }
    ) {
        Column(modifier = Modifier.background(backgroundColor)) {
            if (query.isEmpty()) {
                SuggestionsView { item ->
                    query = item
                    searchTextFieldValue = TextFieldValue(query, TextRange(query.length))

                    coroutineScope.launch {
                        keyboardController?.hide()
                        searchModel.search(item)
                        searchParams.clear()
                    }
                }
            } else if (searchData.value.status != Status.UNINITIALIZED) {
                when (searchData.value.status) {
                    Status.SUCCESS -> {
                        val searchResults = searchData.value.data?.searchResults

                        if (searchResults.isNullOrEmpty()) {
                            NoSearchResultsPlaceholder(query, false, {})
                        } else {
                            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                Row(modifier = Modifier.padding(vertical = 12.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            showFilterPopup = !showFilterPopup

                                            val params = getParams()
                                            if (aggregations.isEmpty()) {
                                                searchModel.getTypes(query, params)
                                                searchModel.getAuthors(query, params)
                                                searchModel.getVolumes(query, params)
                                                searchModel.getBooks(query, params)
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            primarySurfaceColor,
                                            headerTextColor
                                        )
                                    ) {
                                        Text("Filtrează")
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_filter),
                                            contentDescription = "Filtrează",
                                        )
                                    }
                                }

                                Row {
                                    SearchResultsList(
                                        query = query,
                                        hits = searchResults,
                                        navController = navController,
                                        listState = listState,
                                        params = getParams(),
                                        isDark = isDark
                                    )
                                }
                            }
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {}
                    Status.UNINITIALIZED -> TODO()
                }
            } else {
                when (autocompleteData.value.status) {
                    Status.SUCCESS -> {
                        if (autocompleteData.value.data?.hits?.hits.isNullOrEmpty()) {
                            NoSearchResultsPlaceholder(query, true) {
                                if (query.isNotEmpty()) {
                                    keyboardController?.hide()
                                    searchModel.search(query)
                                    searchParams.clear()
                                }
                            }
                        } else {
                            autocompleteData.value.data?.hits?.hits?.let { hits ->
                                AutocompleteList(hits, navController, isDark)
                            }
                        }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {}
                }
            }
        }
    }
    if (showFilterPopup) {
        SearchFilterPopup(
            aggregations = aggregations,
            searchParams,
            onCheck = { category, item, checked ->
                if (checked) {
                    searchParams.putIfAbsent(category, HashSet())
                    searchParams.get(category)?.add(item)
                } else
                    searchParams[category]?.remove(item)

                val params = getParams()
                searchModel.getTypes(query, params)
                searchModel.getAuthors(query, params)
                searchModel.getVolumes(query, params)
                searchModel.getBooks(query, params)

            },
            onSaveAction = {
                showFilterPopup = false

                searchModel.search(query, params = getParams())
                CoroutineScope(Dispatchers.Main).launch {
                    listState.scrollToItem(0, 0)
                }
            },
            onExitAction = { showFilterPopup = false })
    }
}
