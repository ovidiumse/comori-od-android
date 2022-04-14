package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status


@Composable
fun SearchScreen() {
    val searchModel: SearchModel = viewModel()

    var query by remember { mutableStateOf("") }

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Row {
//                        Text("Caută")
//                    }
//                })
//        },
        content = {
            Column {
                SearchBar(
                    searchText = query,
                    onSearchTextChanged = {
                        query = it
                    }, onClearClick = {
                        query = ""
                    })
                if (query.isNotEmpty()) {
                    val autocompleteResponse by searchModel.autocomplete(query)
                        .collectAsState(Resource.loading(null))

                    when (autocompleteResponse.status) {
                        Status.SUCCESS -> {
                            autocompleteResponse.data?.hits?.hits?.let { hits ->
                                AutocompleteList(hits)
                            }
                        }
                        Status.LOADING -> {}
                        Status.ERROR -> {}
                    }
                } else {
                    // Show suggestions
                }

                // On search submit, do
                /*
            val searchResponse by searchModel.search(query).collectAsState(Resource.loading(null))
                when(searchResponse.status) {
                    Status.SUCCESS -> {}
                    Status.LOADING -> {}
                    Status.ERROR -> {}
                }
             */
            }
        })
    }

