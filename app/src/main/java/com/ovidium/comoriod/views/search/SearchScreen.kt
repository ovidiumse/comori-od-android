package com.ovidium.comoriod.views.search

import androidx.appcompat.widget.ActivityChooserView.InnerLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.Screens


@Composable
fun SearchScreen(navController: NavController) {
    val searchModel: SearchModel = viewModel()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
    ) {
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
    }
}

