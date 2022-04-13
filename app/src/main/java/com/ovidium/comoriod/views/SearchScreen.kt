package com.ovidium.comoriod.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun SearchScreen() {
    val searchModel: SearchModel = viewModel()

    var query by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column {
            Surface(modifier = Modifier.fillMaxWidth(), elevation = 8.dp) {

            }

            TextField(
                value = query,
                onValueChange = { newText -> query = newText },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background),
//                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background),
                label = { Text("Caută în Comori OD") })
            if (query.isNotEmpty()) {
                Text("You entered $query")

                val autocompleteResponse by searchModel.autocomplete(query)
                    .collectAsState(Resource.loading(null))

                when (autocompleteResponse.status) {
                    Status.SUCCESS -> {
                        autocompleteResponse.data?.hits?.hits?.forEach { hit -> Column { Text(hit._source.title) } }
                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {}
                }
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
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}