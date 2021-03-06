package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun TitlesForAuthorScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    libraryModel: LibraryModel,
    params: Map<String, String>
) {
    val titlesData by libraryModel.titlesData

    var showFilterPopup by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    Column {
        TitlesForAuthorList(
            libraryModel = libraryModel,
            navController = navController,
            params = params
        )
    }


    if (showFilterPopup) {
        /*libraryModel.titlesForAuthorData.value.data?.aggregations.let { aggregations ->
            SearchFilterPopup(
                aggregations = aggregations,
                searchSource = SearchSource.AUTHOR,
                onCheck = { category, item ->
                    if (libraryModel.searchParams[category] != null && (libraryModel.searchParams[category]!!.contains(item))) {
                        libraryModel.searchParams[category]!!.remove(item)
                    } else if (libraryModel.searchParams[category] != null && !libraryModel.searchParams[category]!!.contains(
                            item
                        )
                    ) {
                        libraryModel.searchParams[category]!!.add(item)
                    } else if (libraryModel.searchParams[category] == null) {
                        libraryModel.searchParams[category] = mutableListOf(item)
                    }
                },
                onSaveAction = {
                    showFilterPopup = false
                    coroutineScope.launch {
                        val types =
                            if (libraryModel.searchParams[FilterCategory.TYPES].isNullOrEmpty()) "" else libraryModel.searchParams[FilterCategory.TYPES]!!.joinToString(
                                ","
                            )
                        val authors =
                            if (libraryModel.searchParams[FilterCategory.AUTHORS].isNullOrEmpty()) "" else libraryModel.searchParams[FilterCategory.AUTHORS]!!.joinToString(
                                ","
                            )
                        val volumes =
                            if (libraryModel.searchParams[FilterCategory.VOLUMES].isNullOrEmpty()) "" else libraryModel.searchParams[FilterCategory.VOLUMES]!!.joinToString(
                                ","
                            )
                        val books =
                            if (libraryModel.searchParams[FilterCategory.BOOKS].isNullOrEmpty()) "" else libraryModel.searchParams[FilterCategory.BOOKS]!!.joinToString(
                                ","
                            )
                        libraryModel.getTitlesForAuthor(
                            authors = authors,
                            types = types,
                            volumes = volumes,
                            books = books
                        )
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        listState.scrollToItem(0, 0)
                    }
                },
                onExitAction = { showFilterPopup = false })
        }*/
    }

}