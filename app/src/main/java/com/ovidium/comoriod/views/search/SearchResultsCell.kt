package com.ovidium.comoriod.views.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
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
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.fmtVerses
import com.ovidium.comoriod.utils.highlightText
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.search.filter.FilterCategory
import java.net.URLEncoder


@Composable
fun SearchResultsCell(
    hit: Hit,
    index: Int,
    navController: NavController,
    searchParams: SnapshotStateMap<FilterCategory, MutableList<String>>
) {

    val searchModel: SearchModel = viewModel()
    val searchData by remember { searchModel.searchData }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(getNamedColor("Container", isDark = isSystemInDarkTheme())!!)
            .clickable {
                navController.navigate(
                    Screens.Article.withArgs(
                        URLEncoder.encode(
                            hit._id,
                            "utf-8"
                        )
                    )
                ) {
                    launchSingleTop = true
                }
            }
    ) {
        SearchResultsTitleView(hit, index)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .wrapContentSize(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                SearchResultsBookView(hit)
                SearchResultsAuthorView(hit)
            }

            SearchResultsTypeView(hit)
        }

        Text(
            text = fmtVerses(hit.highlight.versesText.orEmpty(), isDark = isSystemInDarkTheme()),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 10.dp)
        )
    }

    LaunchedEffect(Unit) {
        val types =
            if (searchParams[FilterCategory.TYPES].isNullOrEmpty()) "" else searchParams[FilterCategory.TYPES]!!.joinToString(
                ","
            )
        val authors =
            if (searchParams[FilterCategory.AUTHORS].isNullOrEmpty()) "" else searchParams[FilterCategory.AUTHORS]!!.joinToString(
                ","
            )
        val volumes =
            if (searchParams[FilterCategory.VOLUMES].isNullOrEmpty()) "" else searchParams[FilterCategory.VOLUMES]!!.joinToString(
                ","
            )
        val books =
            if (searchParams[FilterCategory.BOOKS].isNullOrEmpty()) "" else searchParams[FilterCategory.BOOKS]!!.joinToString(
                ","
            )
        val searchResultsCount =
            searchData.data?.hits?.hits?.count().let { it } ?: return@LaunchedEffect
        val totalHits = searchData.data?.hits?.total?.value.let { it } ?: return@LaunchedEffect
        if ((searchResultsCount < totalHits) && (searchResultsCount == (index + 1))) {
            searchModel.search(
                20,
                searchResultsCount,
                type = types,
                authors = authors,
                volumes = volumes,
                books = books
            )
        }
    }

}


@Composable
fun SearchResultsTitleView(hit: Hit, index: Int) {
    fun getTitle(hit: Hit): String {
        return if (!hit.highlight.title.isNullOrEmpty()) {
            hit.highlight.title[0]
        } else hit._source.title
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${index + 1}.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(start = 16.dp)
        )
        Text(
            text = highlightText(getTitle(hit), isDark = isSystemInDarkTheme()),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            maxLines = 2,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .padding(horizontal = 16.dp)
        )
    }
}


@Composable
fun SearchResultsBookView(hit: Hit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
            contentDescription = "Some icon",
            tint = Color.Red
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = hit._source.book,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
        )
    }
}


@Composable
fun SearchResultsAuthorView(hit: Hit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_author),
            contentDescription = "Some icon",
            tint = Color.Red
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = hit._source.author,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
        )
    }
}


@Composable
fun SearchResultsTypeView(hit: Hit) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .background(Color.LightGray.copy(alpha = 0.6f))
        ) {
            Text(
                text = hit._source.type,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .padding(horizontal = 5.dp)
            )
        }
    }
}