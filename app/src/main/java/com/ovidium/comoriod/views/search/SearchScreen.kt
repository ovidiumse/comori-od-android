package com.ovidium.comoriod.views.search

import SuggestionsView
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.search.filter.SearchFilterPopup
import kotlinx.coroutines.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    searchModel: SearchModel
) {
    var query by remember { searchModel.query }
    var searchTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                query,
                TextRange(query.length)
            )
        )
    }
    var isSearchPending by remember { mutableStateOf(false) }

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
        return searchParams.map { entry -> Pair(entry.key, entry.value.joinToString("|")) }
            .associate { pair -> pair }
    }

    val focusManager = LocalFocusManager.current

    val hasSearchResults = searchData.value.status == Status.SUCCESS

    Scaffold(
        topBar = {
            AppBar(
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState.drawerState) },
                onTitleClicked = {
                    navController.navigate(Screens.Library.route) {
                        launchSingleTop = true
                    }
                },
                actions = @Composable {
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))

                        SearchBar(
                            modifier = Modifier.weight(if (hasSearchResults) 0.9f else 1f),
                            searchText = searchTextFieldValue,
                            shouldFocus = searchData.value.status == Status.UNINITIALIZED,
                            focusRequester = focusRequester,
                            onSearchTextChanged = { newFieldValue ->
                                if (!isSearchPending) {
                                    searchTextFieldValue = newFieldValue
                                    query = newFieldValue.text

                                    searchData.value = Resource.uninitialized()
                                    currentAutocompleteJob?.cancel()

                                    currentAutocompleteJob = coroutineScope.async {
                                        delay(300L)
                                        if (!isSearchPending)
                                            searchModel.autocomplete(query)
                                    }
                                }
                            },
                            onSearchClick = {
                                if (query.isNotEmpty()) {
                                    keyboardController?.hide()

                                    currentAutocompleteJob?.cancel()
                                    autocompleteData.value = Resource.uninitialized()
                                    isSearchPending = true

                                    focusManager.clearFocus()
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
                                isSearchPending = false

                                keyboardController?.show()
                            })

                        if (hasSearchResults) {
                            IconButton(modifier = Modifier
                                .size(48.dp)
                                .weight(0.1f)
                                .align(Alignment.CenterVertically),
                                onClick = {
                                    showFilterPopup = !showFilterPopup

                                    val params = getParams()
                                    if (aggregations.isEmpty()) {
                                        searchModel.getTypes(query, params)
                                        searchModel.getAuthors(query, params)
                                        searchModel.getVolumes(query, params)
                                        searchModel.getBooks(query, params)
                                    }
                                }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_filter),
                                    tint = textColor,
                                    contentDescription = "Filtrează",
                                )
                            }
                        }
                    }
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
                        searchData.value = Resource.loading(null)
                        focusManager.clearFocus()
                        searchModel.search(item)
                        searchParams.clear()
                    }
                }
            } else if (autocompleteData.value.status != Status.UNINITIALIZED) {

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
                    Status.UNINITIALIZED -> {}
                }


            } else {
                when (searchData.value.status) {
                    Status.SUCCESS -> {
                        isSearchPending = false

                        val searchResults = searchData.value.data?.searchResults

                        if (searchResults.isNullOrEmpty()) {
                            NoSearchResultsPlaceholder(query, false, {})
                        } else {
                            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                Row {
                                    SearchResultsList(
                                        searchModel,
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
                    Status.UNINITIALIZED -> {}
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
