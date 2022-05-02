package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.fmtVerses
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.search.filter.FilterCategory
import java.net.URLEncoder

@Composable
fun TitlesForAuthorCell(
    hit: com.ovidium.comoriod.data.titles.Hit,
    index: Int,
    navController: NavController,
    jwtUtils: JWTUtils,
    signInModel: GoogleSignInModel,
    searchParams: SnapshotStateMap<FilterCategory, MutableList<String>>?
) {

    val libraryModel: LibraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel))
    val titlesForAuthor by remember { libraryModel.titlesForAuthorData }

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
        TitlesForAuthorTitleView(hit, index)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .wrapContentSize(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                TitlesForAuthorBookView(hit)
                TitlesForAuthorAuthorView(hit)
            }
            TitlesForAuthorTypeView(hit)
        }
    }

    LaunchedEffect(Unit) {
        val params = searchParams.let { it } ?: return@LaunchedEffect
        val types =
            if (params[FilterCategory.TYPES].isNullOrEmpty()) "" else params[FilterCategory.TYPES]!!.joinToString(
                ","
            )
        val authors =
            if (params[FilterCategory.AUTHORS].isNullOrEmpty()) "" else params[FilterCategory.AUTHORS]!!.joinToString(
                ","
            )
        val volumes =
            if (params[FilterCategory.VOLUMES].isNullOrEmpty()) "" else params[FilterCategory.VOLUMES]!!.joinToString(
                ","
            )
        val books =
            if (params[FilterCategory.BOOKS].isNullOrEmpty()) "" else params[FilterCategory.BOOKS]!!.joinToString(
                ","
            )
        val titlesForAuthorCount =
            titlesForAuthor.data?.hits?.hits?.count().let { it } ?: return@LaunchedEffect
        val titlesTotalHits =
            titlesForAuthor.data?.hits?.total?.value.let { it } ?: return@LaunchedEffect
        println("TITLES: ${titlesForAuthorCount} / ${titlesTotalHits}")
        if ((titlesForAuthorCount < titlesTotalHits) && (titlesForAuthorCount == (index + 1))) {
            libraryModel.getTitlesForAuthor(
                authors = authors,
                types = types,
                volumes = volumes,
                books = books,
                limit = 20,
                offset = titlesForAuthorCount
            )
        }
    }

}


@Composable
fun TitlesForAuthorTitleView(hit: com.ovidium.comoriod.data.titles.Hit, index: Int) {

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
            text = hit._source.title,
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
fun TitlesForAuthorBookView(hit: com.ovidium.comoriod.data.titles.Hit) {
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
fun TitlesForAuthorAuthorView(hit: com.ovidium.comoriod.data.titles.Hit) {
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
fun TitlesForAuthorTypeView(hit: com.ovidium.comoriod.data.titles.Hit) {
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