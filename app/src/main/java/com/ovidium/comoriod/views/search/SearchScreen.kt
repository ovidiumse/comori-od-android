package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.Screens


@Composable
fun SearchScreen(navController: NavController) {
    val searchModel: SearchModel = viewModel()
    val scaffoldState = rememberScaffoldState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            Column {
                SearchBar(
                    searchText = query,
                    onSearchTextChanged = {
                        query = it
                    }, onClearClick = {
                        query = ""
                    }, onDoneClick = {
                        if (query.isNotEmpty())
                            navController.navigate(Screens.SearchResults.withArgs(query))
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
        },
//        topBar = {
//            TopAppBar(
//                title = {
//                    Row {
//                        Text("Caută")
//                    }
//                })
//        },
        scaffoldState = scaffoldState)
    }

