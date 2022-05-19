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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
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
    searchParams: SnapshotStateMap<FilterCategory, MutableList<String>>?,
    isDark: Boolean
) {
    val searchModel: SearchModel = viewModel()
    val searchData by remember { searchModel.searchData }

    val backgroundColor = getNamedColor("PrimarySurface", isDark)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(backgroundColor)
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
        SearchResultsTitleView(hit, index, isDark)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .wrapContentSize(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                SearchResultsBookView(hit, isDark)
                SearchResultsAuthorView(hit, isDark)
            }

            SearchResultsTypeView(hit, isDark)
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
        val searchParams = searchParams.let { it } ?: return@LaunchedEffect
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
fun SearchResultsTitleView(hit: Hit, index: Int, isDark: Boolean) {
    fun getTitle(hit: Hit): String {
        return if (!hit.highlight.title.isNullOrEmpty()) {
            hit.highlight.title[0]
        } else hit._source.title
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.Start)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append("${index + 1}.  ")
                append(highlightText(getTitle(hit), isDark))
            },
            fontSize = 20.sp,
            maxLines = 2,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left
        )
    }
}


@Composable
fun SearchResultsBookView(hit: Hit, isDark: Boolean) {
    val tintColor = getNamedColor("OnBackground", isDark)
    val mutedTextColor = getNamedColor("MutedText", isDark)

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
            contentDescription = "Some icon",
            tint = tintColor
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = hit._source.book,
            fontSize = 12.sp,
            color = mutedTextColor,
            modifier = Modifier
        )
    }
}


@Composable
fun SearchResultsAuthorView(hit: Hit, isDark: Boolean) {
    val tintColor = getNamedColor("OnBackground", isDark)
    val mutedTextColor = getNamedColor("MutedText", isDark)

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_author),
            contentDescription = "Some icon",
            tint = tintColor
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = hit._source.author,
            fontSize = 12.sp,
            color = mutedTextColor,
            modifier = Modifier
        )
    }
}


@Composable
fun SearchResultsTypeView(hit: Hit, isDark: Boolean) {
    val bubbleColor = getNamedColor("Bubble", isDark)

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .background(bubbleColor)
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