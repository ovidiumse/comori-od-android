package com.ovidium.comoriod.views.library

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.model.*
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.library.authors.AuthorPopup
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

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
    Article,
    None
}

class DataItem(
    val title: String,
    val id: String,
    val secondary: String? = null,
    val detail: String? = null,
    val image_url: String? = null,
    val gradient: List<Color>,
    val type: ItemCategory = ItemCategory.None
)

@Composable
fun LibraryScreen(
    navController: NavController,
    signInModel: GoogleSignInModel,
    libraryModel: LibraryModel
) {
    val tabsHeight = 48
    val dropShadowSize = 3
    val isDark = isSystemInDarkTheme()
    val backgroundColor = getNamedColor("Background", isDark)
    val connection by connectivityState()

    val isConnected = connection === ConnectionState.Available

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val authorInfo = remember { mutableStateOf<Bucket?>(null) }

    Column(modifier = Modifier.blur(if (authorInfo.value != null) 16.dp else 0.dp)) {
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
            backgroundColor = getNamedColor("Background", isDark),
            contentColor = getNamedColor("OnBackground", isDark)
        ) {
            TabCategory.values().forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(tab.toString()) })
            }
        }
        HorizontalPager(pageCount = TabCategory.values().size, state = pagerState) { tab ->
            if (isConnected) {
                Surface(modifier = Modifier
                    .background(backgroundColor)
                    .graphicsLayer {
                        val pageOffset = (pagerState.currentPage - tab) + pagerState.currentPageOffsetFraction
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
                        TabCategory.Toate -> LibraryMain(
                            navController,
                            signInModel,
                            libraryModel,
                            isDark
                        ) { bucket -> authorInfo.value = bucket }
                        TabCategory.Autori -> StateHandler(
                            navController,
                            libraryModel.authorsData
                        ) { data, isLoading ->
                            AuthorsGrid(
                                navController,
                                data,
                                isLoading,
                                isDark
                            ) { bucket -> authorInfo.value = bucket }
                        }
                        TabCategory.Volume -> StateHandler(
                            navController,
                            libraryModel.volumesData
                        ) { data, isLoading ->
                            VolumesGrid(navController, data, isLoading, isDark)
                        }
                        TabCategory.Cărți -> StateHandler(
                            navController,
                            libraryModel.booksData
                        ) { data, isLoading ->
                            BooksGrid(navController, data, isLoading, isDark) { true }
                        }
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = "Nu există conexiune la internet. \nTe rugăm să verifici și să revii.",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        maxLines = 2,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    CircularProgressIndicator()
                }
            }
        }
    }
    if (authorInfo.value != null) {
        AuthorPopup(
            navController = navController,
            authorInfo = authorInfo.value!!,
            libraryModel = libraryModel
        ) {
            authorInfo.value = null
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
        Status.UNINITIALIZED -> {}
    }
}



