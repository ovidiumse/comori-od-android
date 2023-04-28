package com.ovidium.comoriod.views.article

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.model.ReadArticlesModel
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.fmtDuration
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ArticleInfoView(article: Article, mutedTextColor: Color, readArticlesModel: ReadArticlesModel, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .height(IntrinsicSize.Max)
    ) {
        Divider(
            color = Color.Red, modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = article.author, fontSize = 14.sp, color = mutedTextColor, modifier = Modifier.padding(bottom = 3.dp)
            )

            if (!article.book.contains(article.volume)) {
                Text(
                    text = article.volume, fontSize = 14.sp, color = mutedTextColor, modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Text(
                text = article.book,
                fontSize = 14.sp,
                color = mutedTextColor,
            )
        }
    }
}