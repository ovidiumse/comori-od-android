package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
    libraryModel: LibraryModel,
    searchParams: SnapshotStateMap<FilterCategory, MutableList<String>>?
) {

    var showFilterPopup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

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
                onMenuClicked = { },
                onFilterClicked = { showFilterPopup = true }
            )
        }
    ) {
        Column() {
            TitlesForAuthorList(
                libraryModel = libraryModel,
                navController = navController,
                searchParams = searchParams
            )
        }


        LaunchedEffect(Unit) {
            if (libraryModel.titlesForAuthorData.value.data == null) {
                val authors =
                    searchParams?.get(FilterCategory.AUTHORS).let { it } ?: return@LaunchedEffect
                val types =
                    searchParams?.get(FilterCategory.TYPES).let { it } ?: return@LaunchedEffect
                libraryModel.getTitlesForAuthor(
                    authors = authors.joinToString(","),
                    types = types.joinToString(",")
                )
            }
        }
    }


    if (showFilterPopup) {
        libraryModel.titlesForAuthorData.value.data?.aggregations.let { aggregations ->
            SearchFilterPopup(
                aggregations = aggregations,
                searchSource = SearchSource.AUTHOR,
                onCheck = { category, item ->
                    if (searchParams?.get(category) != null && (searchParams[category]!!.contains(item))) {
                        searchParams[category]!!.remove(item)
                    } else if (searchParams?.get(category) != null && !searchParams[category]!!.contains(
                            item
                        )
                    ) {
                        searchParams[category]!!.add(item)
                    } else if (searchParams?.get(category) == null) {
                        searchParams?.set(category, mutableListOf(item))
                    }
                },
                onSaveAction = {
                    showFilterPopup = false
                    coroutineScope.launch {
                        val types =
                            if (searchParams?.get(FilterCategory.TYPES).isNullOrEmpty()) "" else searchParams?.get(FilterCategory.TYPES)!!.joinToString(
                                ","
                            )
                        val authors =
                            if (searchParams?.get(FilterCategory.AUTHORS).isNullOrEmpty()) "" else searchParams?.get(FilterCategory.AUTHORS)!!.joinToString(
                                ","
                            )
                        val volumes =
                            if (searchParams?.get(FilterCategory.VOLUMES).isNullOrEmpty()) "" else searchParams?.get(FilterCategory.VOLUMES)!!.joinToString(
                                ","
                            )
                        val books =
                            if (searchParams?.get(FilterCategory.BOOKS).isNullOrEmpty()) "" else searchParams?.get(FilterCategory.BOOKS)!!.joinToString(
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