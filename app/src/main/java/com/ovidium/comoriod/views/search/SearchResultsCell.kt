package com.ovidium.comoriod.views.search

import android.graphics.Paint.Align
import android.text.Html
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.search.Hit


@Composable
fun SearchResultsCell(hit: Hit, index: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Color.DarkGray)
    ) {
        SearchResultsTitleView(hit, index)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 10.dp)
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
            text = buildAnnotatedString {
                val html = hit.highlight.versesText.joinToString(separator = "\n")
                val parsed = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString()
                append(parsed)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 10.dp)
        )
    }
}


@Composable
fun SearchResultsTitleView(hit: Hit, index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    ) {
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
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color.Red, shape = CircleShape)
                    .padding(all = 5.dp)
                    .width(20.dp)
                    .height(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


@Composable
fun SearchResultsBookView(hit: Hit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_book),
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
            modifier = Modifier.size(12.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_book),
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