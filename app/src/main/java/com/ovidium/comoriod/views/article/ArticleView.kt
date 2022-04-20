package com.ovidium.comoriod.views.article

import android.content.res.Resources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.model.ArticleModel
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.fmtVerses
import com.ovidium.comoriod.utils.parseVerse
import kotlinx.coroutines.launch

@Composable
fun ArticleView(articleID: String) {

    val articleModel: ArticleModel = viewModel()
    val articleData by remember { articleModel.articleData }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 64.dp)
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
                Text(
                    text = article.author,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                )
            }
            item {
                Text(
                    text = article.volume,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                )
            }
            item {
                Text(
                    text = article.full_book,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }
            item { 
                Text(
                    text = parseVerse(article.verses, isDark = isSystemInDarkTheme()),
                    fontSize = 20.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }
        }
    }

}