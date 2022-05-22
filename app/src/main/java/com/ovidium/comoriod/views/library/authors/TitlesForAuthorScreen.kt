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
import com.ovidium.comoriod.views.search.filter.SearchFilterPopup
import com.ovidium.comoriod.views.search.filter.SearchSource
import com.ovidium.comoriod.views.search.filter.FilterCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TitlesForAuthorScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    libraryModel: LibraryModel,
) {

    var showFilterPopup by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val isDark = isSystemInDarkTheme()
            SearchTopBar(
                title = {
                    if (!libraryModel.titlesForAuthorData.value.data?.hits?.hits.isNullOrEmpty()) {
                        Text(
                            text = "${libraryModel.titlesForAuthorData.value.data?.hits?.hits?.count()} / ${libraryModel.titlesForAuthorData.value.data?.hits?.total?.value} rezultate",
                            color = getNamedColor("Link", isDark = isDark)
                        )
                    }
                },
                isSearch = true,
                onMenuClicked = { launchMenu(coroutineScope, scaffoldState = scaffoldState) },
                onFilterClicked = { showFilterPopup = true }
            )
        }
    ) {
        Column() {
            TitlesForAuthorList(
                libraryModel = libraryModel,
                navController = navController,
            )
        }
    }


    if (showFilterPopup) {
        libraryModel.titlesForAuthorData.value.data?.aggregations.let { aggregations ->
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
        }
    }

}