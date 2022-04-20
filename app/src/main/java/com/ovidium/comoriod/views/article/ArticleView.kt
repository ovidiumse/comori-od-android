package com.ovidium.comoriod.views.article

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.model.ArticleModel
import com.ovidium.comoriod.utils.*
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

    var openedBibleRef by remember { mutableStateOf("")}

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
                    SelectionContainer {
                        val isDark = isSystemInDarkTheme()
                        val parsedText = parseVerses(article.verses, isDark = isDark)
                        ClickableText(
                            text = parsedText,
                            onClick = { offset ->
                                parsedText.getStringAnnotations(
                                    tag = "URL", start = offset,
                                    end = offset
                                )
                                .firstOrNull()?.let { annotation ->
                                        val bibleRef = article.bibleRefs.get(annotation.item)!!
                                        var bibleRefText = ""
                                        bibleRef.verses.forEach { verse ->
                                            bibleRefText += "${verse.name} - ${verse.text}\n"
                                        }

                                        openedBibleRef = bibleRefText
                                    Log.d("Clicked URL", annotation.item)
                                }

                            }
                        )
                    }
                }
            }
        }

    if (openedBibleRef.isNotEmpty()) {
        Popup(alignment = Alignment.Center) {
            Box(
                Modifier
                    .size(300.dp, 100.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
            ) {
                Text(openedBibleRef)
            }
        }
    }

}