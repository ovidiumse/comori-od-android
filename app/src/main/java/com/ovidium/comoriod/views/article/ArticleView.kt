@file:OptIn(ExperimentalTime::class)

package com.ovidium.comoriod.views.article

import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.article.SearchArticleResponse
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.model.*
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.highlightBody
import com.ovidium.comoriod.utils.parseVerses
import kotlin.time.ExperimentalTime


@Composable
fun ArticleView(
    articleID: String,
    markupId: String? = null,
    isSearch: String? = null,
    receivedMarkupIndex: MutableState<Int>,
    receivedMarkupLength: MutableState<Int>,
    signInModel: GoogleSignInModel,
    favoritesModel: FavoritesModel,
    searchModel: SearchModel,
    markupsModel: MarkupsModel,
    readArticlesModel: ReadArticlesModel,
    bgColor: Color = getNamedColor("Background", isSystemInDarkTheme())
) {
    val bookModel: BookModel = viewModel()
    val bookData = remember { bookModel.bookData }
    val searchData = remember { bookModel.searchData }
    val query by remember { searchModel.query }
    val highlights = remember { mutableStateListOf<TextRange>() }
    val offsetList = remember { mutableStateListOf<Int>() }
    val currentHighlightIndex = remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val articleData = bookData.getOrDefault(articleID, Resource.uninitialized())
        val searchArticleData = searchData.getOrDefault(articleID, Resource.uninitialized())

        @Composable
        fun buildArticle(currentArticle: ArticleResponse, markups: List<Markup>): Article {
            return Article(
                currentArticle._id,
                highlightBody(currentArticle.title, isSystemInDarkTheme()),
                currentArticle.author,
                currentArticle.volume,
                currentArticle.book,
                currentArticle.type,
                parseVerses(
                    currentArticle.verses,
                    markups,
                    highlights,
                    currentHighlightIndex.value,
                    isDark = isSystemInDarkTheme(),
                    coroutineScope
                ),
                currentArticle.bibleRefs,
                currentArticle.read_time
            )
        }

        @Composable
        fun buildArticle(currentArticle: SearchArticleResponse, markups: List<Markup>): Article {
            return Article(
                currentArticle._id,
                highlightBody(currentArticle._source.title, isSystemInDarkTheme()),
                currentArticle._source.author,
                currentArticle._source.volume,
                currentArticle._source.book,
                currentArticle._source.type,
                parseVerses(
                    currentArticle._source.verses,
                    markups,
                    highlights,
                    currentHighlightIndex.value,
                    isDark = isSystemInDarkTheme(),
                    coroutineScope
                ),
                currentArticle._source.bibleRefs,
                currentArticle._source.read_time
            )
        }

        @Composable
        fun <T> showArticle(articleID: String, currentArticle: Resource<T>) {
            when (currentArticle.status) {
                Status.SUCCESS -> {
                    currentArticle.data?.let { data ->
                        val markups =
                            markupsModel.markups.value.data?.filter { markup -> markup.articleID == articleID }
                                ?: emptyList()

                        val article = if (currentArticle.data is ArticleResponse) buildArticle(
                            currentArticle.data as ArticleResponse,
                            markups
                        ) else buildArticle(currentArticle.data as SearchArticleResponse, markups)

                        ArticleViewContent(
                            article,
                            markupId,
                            isSearch,
                            markups,
                            highlights,
                            offsetList,
                            currentHighlightIndex,
                            receivedMarkupIndex,
                            receivedMarkupLength,
                            signInModel,
                            favoritesModel,
                            markupsModel,
                            readArticlesModel,
                            bgColor
                        )
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {}
                Status.UNINITIALIZED -> {}
            }
        }

        if (articleData.status != Status.UNINITIALIZED)
            showArticle(articleID, articleData)
        else
            showArticle(articleID, searchArticleData)
    }
    LaunchedEffect(Unit) {
        bookModel.getArticle(articleID, query, isSearch)
    }
}

