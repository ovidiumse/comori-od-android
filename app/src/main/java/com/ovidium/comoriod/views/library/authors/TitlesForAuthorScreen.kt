package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.SearchTopBar
import com.ovidium.comoriod.launchMenu
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.fmtVerses
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.search.SearchFilterPopup
import com.ovidium.comoriod.views.search.SearchSource
import com.ovidium.comoriod.views.search.filter.FilterCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

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
                            color = getNamedColor("Link", isDark = isDark)!!
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