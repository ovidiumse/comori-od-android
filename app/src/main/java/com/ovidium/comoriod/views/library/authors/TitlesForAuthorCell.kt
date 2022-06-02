package com.ovidium.comoriod.views.library.authors

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
import com.ovidium.comoriod.data.titles.TitleHit
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.search.filter.FilterCategory
import java.net.URLEncoder

@Composable
fun TitlesForAuthorCell(
    hit: TitleHit,
    index: Int,
    navController: NavController,
    libraryModel: LibraryModel,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(getNamedColor("Container", isDark = isSystemInDarkTheme()))
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
        val params = libraryModel.searchParams.let { it }
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
        val totalHits = libraryModel.titlesForAuthorData.value.data?.hits?.total?.value ?: return@LaunchedEffect
        val hitsCount = libraryModel.titlesForAuthorData.value.data?.hits?.hits?.count() ?: return@LaunchedEffect
        if ((hitsCount < totalHits) && (hitsCount == (index + 1))) {
            libraryModel.getTitlesForAuthor(
                authors = authors,
                types = types,
                volumes = volumes,
                books = books,
                limit = 20,
                offset = hitsCount
            )
        }
    }

}


@Composable
fun TitlesForAuthorTitleView(hit: TitleHit, index: Int) {

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
fun TitlesForAuthorBookView(hit: TitleHit) {
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
fun TitlesForAuthorAuthorView(hit: TitleHit) {
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
fun TitlesForAuthorTypeView(hit: TitleHit) {
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