package com.ovidium.comoriod.views.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import retrofit2.http.Query

@Composable
fun SearchResultsScreen(query: String) {

    val searchModel: SearchModel = viewModel()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            val searchResponse by searchModel.search(query).collectAsState(Resource.loading(null))
            when(searchResponse.status) {
                Status.SUCCESS -> {
                    searchResponse.data?.hits?.hits?.let { hits ->
                        SearchResultsList(hits = hits)
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {}
            }

        },
        scaffoldState = scaffoldState)
    
}