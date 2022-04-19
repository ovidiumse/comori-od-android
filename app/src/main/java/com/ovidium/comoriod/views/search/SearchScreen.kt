package com.ovidium.comoriod.views.search

import SuggestionsView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.colors
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.CoroutineScope
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

    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
    ) {
        SearchBar(
            searchText = query,
            onSearchTextChanged = {
                query = it
                isSearch = false
                coroutineScope.launch {
                    searchModel.autocomplete()
                }
            }, onClearClick = {
                query = ""
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
            SuggestionsView(coroutineScope, keyboardController)
        }
    }
}