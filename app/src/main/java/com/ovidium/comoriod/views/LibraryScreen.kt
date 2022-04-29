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

class DataItem(
    val title: String,
    val secondary: String? = null,
    val detail: String? = null,
    val imageId: Int? = null,
    val gradient: List<Color>
) {
}

@Composable
fun LibraryScreen(jwtUtils: JWTUtils, signInModel: GoogleSignInModel) {
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
                    TabCategory.Toate -> LibraryMain(signInModel, libraryModel, isDark)
                    TabCategory.Autori -> StateHandler(libraryModel.authorsData) { data, isLoading ->
                        AuthorsGrid(data, isLoading, isDark)
                    }
                    TabCategory.Volume -> StateHandler(libraryModel.volumesData) { data, isLoading ->
                        VolumesGrid(data, isLoading, isDark)
                    }
                    TabCategory.Cărți -> StateHandler(libraryModel.booksData) { data, isLoading ->
                        BooksGrid(data, isLoading, isDark)
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryMain(signInModel: GoogleSignInModel, libraryModel: LibraryModel, isDark: Boolean) {

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        if (signInModel.userResource.value.state == UserState.LoggedIn) {
            item {
                StateHandler(libraryModel.recentlyAddedBooksData) { data, isLoading ->
                    RecentlyAddedBooksRow(data, isLoading, isDark)
                }
            }
            item {
                StateHandler(libraryModel.recommendedData) { data, isLoading ->
                    RecommendedRow(data, isLoading, isDark)
                }
            }
            item {
                StateHandler(libraryModel.trendingData) { data, isLoading ->
                    TrendingRow(data, isLoading, isDark)
                }
            }
        }
        item {
            StateHandler(libraryModel.authorsData) { data, isLoading ->
                AuthorsRow(data, isLoading, isDark)
            }
        }
        item {
            StateHandler(libraryModel.volumesData) { data, isLoading ->
                VolumesRow(data, isLoading, isDark)
            }
        }
    }
}

@Composable
fun HorizontalCarousel(
    name: String,
    dataItems: List<DataItem>?,
    estimatedSize: Int,
    isLoading: Boolean
) {
    val itemMinWidth = 180
    val marginSize = 12
    var screenWidth = LocalConfiguration.current.screenWidthDp
    val itemsByRow = screenWidth / itemMinWidth

    fun calculateItemSize(screenWidth: Int): Int {
        return (screenWidth - (itemsByRow + 1) * marginSize) / itemsByRow
    }

    var itemSize = calculateItemSize(screenWidth)
    val itemCnt = dataItems?.size ?: estimatedSize
    if (itemCnt > itemsByRow) {
        screenWidth -= (itemSize * 0.3).toInt()
        itemSize = calculateItemSize(screenWidth)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(marginSize.dp),
        modifier = Modifier
            .fillMaxHeight()
            .padding(marginSize.dp)
    ) {
        Text(
            name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
            if (isLoading)
                repeat(estimatedSize) {
                    item() {
                        ItemCard(
                            title = "",
                            isLoading = isLoading,
                            colors = emptyList(),
                            itemSize = itemSize,
                            marginSize = marginSize
                        )
                    }
                }
            else {
                dataItems?.let {
                    items(dataItems) { dataItem ->
                        ItemCard(
                            dataItem.title,
                            isLoading,
                            dataItem.secondary,
                            dataItem.detail,
                            dataItem.imageId,
                            dataItem.gradient,
                            itemSize,
                            marginSize
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun AuthorsRow(response: AuthorsResponse?, isLoading: Boolean, isDark: Boolean) {
    val items = response?.aggregations?.authors?.buckets?.map { bucket ->
        DataItem(
            bucket.key,
            "",
            "",
            getDrawableByAuthor(bucket.key),
            getVolumeCoverGradient("", isDark)
        )
    }

    AuthorsCarousel("Autori", items, 2, isLoading)
}

@Composable
fun AuthorsCarousel(
    name: String,
    dataItems: List<DataItem>?,
    estimatedSize: Int,
    isLoading: Boolean
) {
    val itemMinWidth = 180
    val marginSize = 12
    var screenWidth = LocalConfiguration.current.screenWidthDp
    val itemsByRow = screenWidth / itemMinWidth

    fun calculateItemSize(screenWidth: Int): Int {
        return (screenWidth - (itemsByRow + 1) * marginSize) / itemsByRow
    }

    var itemSize = calculateItemSize(screenWidth)
    val itemCnt = dataItems?.size ?: estimatedSize
    if (itemCnt > itemsByRow) {
        screenWidth -= (itemSize * 0.3).toInt()
        itemSize = calculateItemSize(screenWidth)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(marginSize.dp),
        modifier = Modifier
            .fillMaxHeight()
            .padding(marginSize.dp)
    ) {
        Text(
            name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
            if (isLoading)
                repeat(estimatedSize) {
                    item() {
                        AuthorCard(
                            title = "",
                            isLoading = isLoading,
                            colors = emptyList(),
                            itemSize = itemSize,
                            marginSize = marginSize
                        )
                    }
                }
            else {
                dataItems?.let {
                    items(dataItems) { dataItem ->
                        AuthorCard(
                            dataItem.title,
                            isLoading,
                            dataItem.imageId,
                            dataItem.gradient,
                            itemSize,
                            marginSize
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun AuthorCard(
    title: String,
    isLoading: Boolean,
    imageId: Int? = null,
    colors: List<Color>,
    itemSize: Int,
    marginSize: Int
) {
    val imageAreaSize = itemSize * 0.80
    val authorImageSize = imageAreaSize * 0.80

    Card(Modifier.clip(RoundedCornerShape(8))) {
        var boxModifier = Modifier
            .size(width = itemSize.dp, height = itemSize.dp)
            .placeholder(
                isLoading,
                color = Color.DarkGray,
                highlight = PlaceholderHighlight.fade(highlightColor = Color.LightGray)
            )

        if (!isLoading)
            boxModifier = boxModifier.background(brush = Brush.verticalGradient(colors))

        Box(boxModifier) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(20.dp))
                imageId?.let {
                    Image(
                        painter = painterResource(imageId),
                        contentDescription = "details",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .requiredSize(authorImageSize.dp)
                            .clip(RoundedCornerShape(100))
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    color = Color.Black,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(horizontal = marginSize.dp)
                )
            }
        }
    }
}

@Composable
fun TrendingRow(response: TrendingResponse?, isLoading: Boolean, isDark: Boolean) {
    val items = response?.map { item ->
        DataItem(
            item.title,
            item.author,
            item.book,
            getDrawableByAuthor(item.author),
            getVolumeCoverGradient(item.volume, isDark),
        )
    }

    val show = isLoading || (items != null && items.isNotEmpty())
    if (show)
        HorizontalCarousel("Populare acum", items, 5, isLoading)
}

@Composable
fun RecentlyAddedBooksRow(response: RecentlyAddedBooksResponse?, isLoading: Boolean, isDark: Boolean) {
    fun getAuthor(item: RecentlyAddedBooksResponseItem) : String? {
        return if (item.authors.isEmpty()) null else item.authors[0]
    }

    val items = response?.map { item ->
        val author = getAuthor(item)

        DataItem(
            item.name,
            author,
            item.volume,
            author?.let { getDrawableByAuthor(author) },
            getVolumeCoverGradient(item.volume, isDark),
        )
    }

    val show = isLoading || (items != null && items.isNotEmpty())
    if (show)
        HorizontalCarousel("Adăugate recent", items, 2, isLoading)
}

@Composable
fun RecommendedRow(response: RecommendedResponse?, isLoading: Boolean, isDark: Boolean) {
    val items = response?.map { item ->
        DataItem(
            item.title,
            item.author,
            item.book,
            getDrawableByAuthor(item.author),
            getVolumeCoverGradient(item.volume, isDark)
        )
    }

    val show = isLoading || (items != null && items.isNotEmpty())
    if (show)
        HorizontalCarousel("Recomandate", items, 5, isLoading)
}

@Composable
fun VolumesRow(response: VolumesResponse?, isLoading: Boolean, isDark: Boolean) {
    fun getAuthor(bucket: Bucket): String? {
        val authors = bucket.authors.buckets
        return if (authors.isEmpty()) null else authors[0].key
    }

    val items = response?.aggregations?.volumes?.buckets?.map { bucket ->
        val author = getAuthor(bucket)
        val imageId = if (author != null) getDrawableByAuthor(author) else null
        DataItem(
            bucket.key,
            author,
            imageId = imageId,
            gradient = getVolumeCoverGradient(bucket.key, isDark)
        )
    }

    HorizontalCarousel(name = "Volume", items, estimatedSize = 7, isLoading)
}

@Composable
fun AuthorsGrid(response: AuthorsResponse?, isLoading: Boolean, isDark: Boolean) {
    fun getBooksNumber(bucket: com.ovidium.comoriod.data.authors.Bucket): String {
        return articulate(bucket.books.buckets.size, "cărți", "carte")
    }

    val items = response?.aggregations?.authors?.buckets?.map { bucket ->
        DataItem(
            title = bucket.key,
            detail = getBooksNumber(bucket),
            imageId = getDrawableByAuthor(bucket.key),
            gradient = getVolumeCoverGradient("", isDark = isDark)
        )
    }

    val itemMinWidth = 180
    val marginSize = 12
    val itemsByRow = LocalConfiguration.current.screenWidthDp / itemMinWidth
    val itemSize =
        (LocalConfiguration.current.screenWidthDp - (itemsByRow + 1) * marginSize) / itemsByRow
    val estimatedSize = 2

    LazyColumn(
        contentPadding = PaddingValues(horizontal = marginSize.dp, vertical = marginSize.dp),
        verticalArrangement = Arrangement.spacedBy(marginSize.dp),
        modifier = Modifier
            .fillMaxHeight()
    ) {
        if (isLoading) {
            repeat(ceil(estimatedSize.toDouble() / itemsByRow).toInt()) {
                item() {
                    Row(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
                        repeat(itemsByRow) {
                            AuthorCard(
                                title = "",
                                isLoading,
                                itemSize = itemSize,
                                colors = emptyList(),
                                marginSize = marginSize
                            )
                        }

                    }
                }
            }
        } else {
            items?.let {
                items(items.chunked(itemsByRow)) { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
                        for (item in rowItems) {
                            AuthorCard(
                                item.title,
                                isLoading,
                                item.imageId,
                                item.gradient,
                                itemSize,
                                marginSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VolumesGrid(response: VolumesResponse?, isLoading: Boolean, isDark: Boolean) {
    fun getAuthor(bucket: Bucket): String? {
        val authors = bucket.authors.buckets
        return if (authors.isEmpty()) null else authors[0].key
    }

    val items = response?.aggregations?.volumes?.buckets?.map { bucket ->
        val author = getAuthor(bucket)
        DataItem(
            title = bucket.key,
            secondary = author,
            imageId = author?.let { getDrawableByAuthor(author) },
            gradient = getVolumeCoverGradient(bucket.key, isDark)
        )
    }

    ItemsGrid(names = Pair("volum", "volume"), items, estimatedSize = 12, isLoading = isLoading)
}

@Composable
fun BooksGrid(response: BooksResponse?, isLoading: Boolean, isDark: Boolean) {
    fun getVolume(bucket: com.ovidium.comoriod.data.books.Bucket): String {
        val volumes = bucket.volumes.buckets
        return if (volumes.isEmpty()) "" else volumes[0].key
    }

    fun getGradient(bucket: com.ovidium.comoriod.data.books.Bucket): List<Color> {
        return getVolumeCoverGradient(getVolume(bucket), isDark)
    }

    fun getAuthor(bucket: com.ovidium.comoriod.data.books.Bucket): String? {
        val authors = bucket.authors.buckets
        return if (authors.isEmpty()) null else authors[0].key
    }

    val items = response?.aggregations?.books?.buckets?.map { bucket ->
        val author = getAuthor(bucket)
        val volume = getVolume(bucket)
        DataItem(
            title = bucket.key,
            secondary = author,
            detail = volume,
            imageId = author?.let { getDrawableByAuthor(author) },
            gradient = getGradient(bucket)
        )
    }

    ItemsGrid(names = Pair("carte", "cărți"), items, estimatedSize = 50, isLoading = isLoading)
}

@Composable
fun <T> StateHandler(
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

@Composable
fun ItemsGrid(
    names: Pair<String, String>,
    items: List<DataItem>?,
    estimatedSize: Int,
    isLoading: Boolean = false,
) {
    val itemMinWidth = 180
    val marginSize = 12
    val itemsByRow = LocalConfiguration.current.screenWidthDp / itemMinWidth
    val itemSize =
        (LocalConfiguration.current.screenWidthDp - (itemsByRow + 1) * marginSize) / itemsByRow

    LazyColumn(
        contentPadding = PaddingValues(horizontal = marginSize.dp, vertical = marginSize.dp),
        verticalArrangement = Arrangement.spacedBy(marginSize.dp),
        modifier = Modifier
            .fillMaxHeight()
    ) {
        if (isLoading) {
            repeat(ceil(estimatedSize.toDouble() / itemsByRow).toInt()) {
                item() {
                    Row(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
                        repeat(itemsByRow) {
                            ItemCard(
                                title = "",
                                isLoading,
                                itemSize = itemSize,
                                colors = emptyList(),
                                marginSize = marginSize
                            )
                        }

                    }
                }
            }
        } else {
            items?.let {
                fun renderItems(items: List<DataItem>) {
                    items(items.chunked(itemsByRow)) { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
                            for (item in rowItems) {
                                ItemCard(
                                    item.title,
                                    isLoading = isLoading,
                                    item.secondary,
                                    item.detail,
                                    item.imageId,
                                    item.gradient,
                                    itemSize,
                                    marginSize
                                )
                            }
                        }
                    }
                }

                val hasDetail = items.isNotEmpty() && items[0].detail != null
                val hasSecondary = items.isNotEmpty() && items[0].secondary != null

                if (hasDetail || hasSecondary) {
                    val grouped = items.groupBy { item -> item.detail ?: item.secondary }

                    grouped.forEach { (group, items) ->
                        stickyHeader {
                            Card(
                                modifier = Modifier
                                    .offset(x = 1.dp, y = 1.dp)
                                    .wrapContentWidth()
                                    .clip(RoundedCornerShape(30))
                            ) {
                                Text(
                                    group.toString() + " - " + articulate(
                                        items.size,
                                        names.second,
                                        names.first
                                    ),
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }

                        renderItems(items)
                    }
                } else
                    renderItems(items)
            }
        }
    }
}

@Composable
fun ItemCard(
    title: String,
    isLoading: Boolean,
    secondary: String? = null,
    detail: String? = null,
    imageId: Int? = null,
    colors: List<Color>,
    itemSize: Int,
    marginSize: Int
) {
    val titleAreaSize = itemSize * 0.60
    val authorBarSize = itemSize * 0.18
    val authorImageSize = authorBarSize * 1.60
    val authorImageLeftOffset = authorBarSize * 0.3

    Card(Modifier.clip(RoundedCornerShape(8))) {
        var boxModifier = Modifier
            .size(width = itemSize.dp, height = itemSize.dp)
            .placeholder(
                isLoading,
                color = Color.DarkGray,
                highlight = PlaceholderHighlight.fade(highlightColor = Color.LightGray)
            )

        if (!isLoading)
            boxModifier = boxModifier.background(brush = Brush.verticalGradient(colors))

        Box(boxModifier) {
            Column {
                Row(
                    modifier = Modifier
                        .height(titleAreaSize.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth()
                            .padding(horizontal = marginSize.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color.DarkGray)
                        .height(authorBarSize.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        imageId?.let {
                            Image(
                                painter = painterResource(imageId),
                                contentDescription = "details",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .offset(x = authorImageLeftOffset.dp)
                                    .requiredSize(authorImageSize.dp)
                                    .clip(RoundedCornerShape(100))
                            )
                        }

                        secondary?.let {
                            Text(
                                text = secondary,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                color = Color.White
                            )
                        }
                    }
                }

                detail?.let {
                    Row(modifier = Modifier.fillMaxHeight()) {
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.caption,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}