package com.ovidium.comoriod.views.article

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.solver.widgets.Rectangle
import androidx.lifecycle.viewmodel.compose.*
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.model.ArticleModel
import com.ovidium.comoriod.ui.theme.colors
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.*

@Composable
fun ArticleView(articleID: String) {

    val articleModel: ArticleModel = viewModel()
    val articleData by remember { articleModel.articleData }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        when (articleData.status) {
            Status.SUCCESS -> {
                articleData.data?.let { article ->
                    ArticleViewContent(article)
                }
            }
            Status.LOADING -> {}
            Status.ERROR -> {}
        }
    }
    LaunchedEffect(Unit) {
        articleModel.getArticle(articleID)
    }
}


@Composable
fun ArticleViewContent(article: ArticleResponse) {

    val bibleRefs = remember { mutableStateListOf<BibleRefVerse>() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = article.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .padding(bottom = 16.dp)
                ) {
                    Divider(
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Text(
                            text = article.author,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(bottom = 3.dp)
                        )
                        Text(
                            text = article.volume,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(bottom = 3.dp)
                        )
                        Text(
                            text = article.full_book,
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                }
            }
            item {
                SelectionContainer {
                    val isDark = isSystemInDarkTheme()
                    val parsedText = parseVerses(article.verses, isDark = isDark)
                    ClickableText(
                        text = parsedText,
                        style = TextStyle(
                            color = MaterialTheme.colors.onBackground,
                            fontSize = 18.sp,
                            lineHeight = 25.sp
                        ),
                        onClick = { offset ->
                            val annotation = parsedText.getStringAnnotations(
                                tag = "URL",
                                start = offset,
                                end = offset
                            ).firstOrNull()

                            bibleRefs.clear()

                            if (annotation != null)
                                bibleRefs.addAll(article.bibleRefs[annotation.item]!!.verses)
                        }
                    )
                }
            }
        }
    }

    if (bibleRefs.isNotEmpty()) {
        BibleRefsPopup(bibleRefs)
    }
}