package com.ovidium.comoriod.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.material.placeholder
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.books.BooksResponse
import com.ovidium.comoriod.data.recentlyaddedbooks.RecentlyAddedBooksResponse
import com.ovidium.comoriod.data.recentlyaddedbooks.RecentlyAddedBooksResponseItem
import com.ovidium.comoriod.data.recommended.RecommendedResponse
import com.ovidium.comoriod.data.trending.TrendingResponse
import com.ovidium.comoriod.data.volumes.Bucket
import com.ovidium.comoriod.data.volumes.VolumesResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.*
import com.ovidium.comoriod.views.library.AuthorsGrid
import com.ovidium.comoriod.views.library.BooksGrid
import com.ovidium.comoriod.views.library.LibraryMain
import com.ovidium.comoriod.views.library.VolumesGrid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.ceil

val TAG = "LibraryScreen"

enum class TabCategory(val value: Int) {
    Toate(0),
    Autori(1),
    Volume(2),
    Cărți(3)
}

enum class ItemCategory {
    Author,
    Volume,
    Book,
    Article
}

class DataItem(
    val title: String,
    val secondary: String? = null,
    val detail: String? = null,
    val imageId: Int? = null,
    val gradient: List<Color>,
    val type: ItemCategory = ItemCategory.Article
) {
}

@Composable
fun LibraryScreen(navController: NavController, jwtUtils: JWTUtils, signInModel: GoogleSignInModel) {
    val tabsHeight = 40
    val dropShadowSize = 3
    val isDark = isSystemInDarkTheme()

    val libraryModel: LibraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel))
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column() {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(
                        pagerState,
                        tabPositions
                    )
                )
            },
            modifier = Modifier
                .height(tabsHeight.dp)
                .shadow(dropShadowSize.dp),
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        ) {
            TabCategory.values().forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(tab.toString()) })
            }
        }

        HorizontalPager(count = TabCategory.values().size, state = pagerState) { tab ->
            Card(modifier = Modifier.graphicsLayer {
                val pageOffset = calculateCurrentOffsetForPage(tab).absoluteValue

                lerp(
                    start = ScaleFactor(0.85f, 0.85f),
                    stop = ScaleFactor(1f, 1f),
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale.scaleX
                    scaleY = scale.scaleY
                }

                alpha = lerp(
                    start = ScaleFactor(0.5f, 0.5f),
                    stop = ScaleFactor(1f, 1f),
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).scaleX
            }) {
                when (TabCategory.values()[tab]) {
                    TabCategory.Toate -> LibraryMain(navController, signInModel, libraryModel, isDark)
                    TabCategory.Autori -> StateHandler(navController, libraryModel.authorsData) { data, isLoading ->
                        AuthorsGrid(navController, data, isLoading, isDark)
                    }
                    TabCategory.Volume -> StateHandler(navController, libraryModel.volumesData) { data, isLoading ->
                        VolumesGrid(navController, data, isLoading, isDark)
                    }
                    TabCategory.Cărți -> StateHandler(navController, libraryModel.booksData) { data, isLoading ->
                        BooksGrid(navController, data, isLoading, isDark)
                    }
                }
            }
        }
    }
}


@Composable
fun <T> StateHandler(
    navController: NavController,
    responseData: SharedFlow<Resource<T>>,
    showSuccess: @Composable (T?, Boolean) -> Unit
) {
    val response by responseData.collectAsState(initial = Resource.loading(null))

    when (response.status) {
        Status.SUCCESS, Status.LOADING -> showSuccess(
            response.data,
            response.status == Status.LOADING
        )
        Status.ERROR -> Toast.makeText(
            LocalContext.current,
            "Ceva nu a mers bine!",
            Toast.LENGTH_SHORT
        ).show()
    }
}



