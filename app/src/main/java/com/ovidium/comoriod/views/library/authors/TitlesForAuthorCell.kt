package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import java.net.URLEncoder

@Composable
fun TitlesForAuthorCell(
    hit: TitleHit,
    index: Int,
    navController: NavController,
    bgColor: Color,
    mutedTextColor: Color,
    bubbleColor: Color,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(12.dp)
            .clickable {
                navController.navigate(
                    Screens.Article.withArgs(URLEncoder.encode(hit._id, "utf-8"))
                ) {
                    launchSingleTop = true
                }
            }
    ) {
        TitlesForAuthorTitleView(hit, index, mutedTextColor)
        Row(
            modifier = Modifier.wrapContentSize(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                TitlesForAuthorBookView(hit, mutedTextColor)
                TitlesForAuthorAuthorView(hit, mutedTextColor)
            }
            TitlesForAuthorTypeView(hit, mutedTextColor, bubbleColor)
        }
    }
}


@Composable
fun TitlesForAuthorTitleView(hit: TitleHit, index: Int, mutedTextColor: Color) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = mutedTextColor)) {
                    append("${index + 1}.  ")
                }

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(hit._source.title)
                }
            },
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Left
        )
    }
}


@Composable
fun TitlesForAuthorBookView(hit: TitleHit, mutedTextColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
            contentDescription = "Book icon",
            tint = mutedTextColor.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = hit._source.book,
            style = MaterialTheme.typography.subtitle2,
            color = mutedTextColor,
            modifier = Modifier
        )
    }
}


@Composable
fun TitlesForAuthorAuthorView(hit: TitleHit, mutedTextColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_author),
            contentDescription = "Author icon",
            tint = mutedTextColor.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = hit._source.author,
            style = MaterialTheme.typography.subtitle2,
            color = mutedTextColor,
            modifier = Modifier
        )
    }
}


@Composable
fun TitlesForAuthorTypeView(hit: TitleHit, mutedTextColor: Color, bubbleColor: Color) {
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
                color = mutedTextColor,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .padding(horizontal = 5.dp)
            )
        }
    }
}