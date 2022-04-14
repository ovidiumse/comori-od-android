package com.ovidium.comoriod.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.items
import com.ovidium.comoriod.data.autocomplete.Hit
import com.ovidium.comoriod.data.autocomplete.Hits
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.search.AutocompleteList
import com.ovidium.comoriod.views.search.SearchBar
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
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

