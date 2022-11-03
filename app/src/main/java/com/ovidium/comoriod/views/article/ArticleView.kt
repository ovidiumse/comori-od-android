@file:OptIn(ExperimentalTime::class)

package com.ovidium.comoriod.views.article

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.model.*
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
    signInModel: GoogleSignInModel,
    favoritesModel: FavoritesModel,
    searchModel: SearchModel,
    markupsModel: MarkupsModel
) {
    val bookModel: BookModel = viewModel()
    val bookData = remember { bookModel.bookData }
    val searchData = remember { bookModel.searchData }
    val query by remember { searchModel.query }
    val highlights = remember { mutableStateListOf<TextRange>() }
    val offsetList = remember { mutableStateListOf<Int>() }
    val currentHighlightIndex = remember { mutableStateOf<Int?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val articleData = bookData.getOrDefault(articleID, Resource.loading(data = null))
        val searchArticleData = searchData.getOrDefault(articleID, Resource.loading(data = null))
        when (articleData.status) {
            Status.SUCCESS -> {
                articleData.data?.let { data ->
                    val markups = markupsModel.markups.value.data?.filter { markup -> markup.articleID == data._id } ?: emptyList()
                    val article = Article(data._id, highlightBody(data.title, isSystemInDarkTheme()), data.author, data.volume, data.book, data.type, parseVerses(data.verses, markups, highlights, currentHighlightIndex.value, isDark = isSystemInDarkTheme()), data.bibleRefs)
                    ArticleViewContent(
                        article,
                        markupId,
                        isSearch,
                        markups,
                        highlights,
                        offsetList,
                        currentHighlightIndex,
                        signInModel,
                        favoritesModel,
                        markupsModel
                    )
                }
            }
            Status.LOADING -> {}
            Status.ERROR -> {}
        }

        when (searchArticleData.status) {
            Status.SUCCESS -> {
                searchArticleData.data?.let { data ->
                    val markups = markupsModel.markups.value.data?.filter { markup -> markup.articleID == data._id } ?: emptyList()
                    val article = Article(data._id, highlightBody(data._source.title, isSystemInDarkTheme()), data._source.author, data._source.volume, data._source.book, data._source.type, parseVerses(data._source.verses, markups, highlights, currentHighlightIndex.value, isDark = isSystemInDarkTheme()), data._source.bibleRefs)
                    ArticleViewContent(
                        article,
                        markupId,
                        isSearch,
                        markups,
                        highlights,
                        offsetList,
                        currentHighlightIndex,
                        signInModel,
                        favoritesModel,
                        markupsModel
                    )
                }
            }
            Status.LOADING -> {
                Text("Loading...")
            }
            Status.ERROR -> {}
        }
    }
    LaunchedEffect(Unit) {
        bookModel.getArticle(articleID, query, isSearch)
    }
}

