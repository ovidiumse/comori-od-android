@file:OptIn(ExperimentalTime::class)

package com.ovidium.comoriod.views.article

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.CustomTextToolbar
import com.ovidium.comoriod.components.selection.SelectionContainer
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.model.*
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.highlightText
import com.ovidium.comoriod.utils.parseVerses
import com.ovidium.comoriod.views.favorites.SaveFavoriteDialog
import com.ovidium.comoriod.views.markups.SaveMarkupDialog
import kotlin.time.ExperimentalTime

@Composable
fun ArticleView(
    articleID: String,
    markupId: String? = null,
    signInModel: GoogleSignInModel,
    favoritesModel: FavoritesModel,
    searchModel: SearchModel,
    markupsModel: MarkupsModel
) {
    val articleModel: BookModel = viewModel()
    val bookData = remember { articleModel.bookData }
    val searchData = remember { articleModel.searchData }
    var query by remember { searchModel.query }

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
                    val article = Article(data._id, highlightText(data.title, isSystemInDarkTheme()), data.author, data.volume, data.book, data.type, parseVerses(data.verses, emptyList(), isDark = isSystemInDarkTheme()), data.bibleRefs)
                    ArticleViewContent(
                        article,
                        markupId,
                        markups,
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
                    val article = Article(data._id, highlightText(data._source.title, isSystemInDarkTheme()), data._source.author, data._source.volume, data._source.book, data._source.type, parseVerses(data._source.verses, emptyList(), isDark = isSystemInDarkTheme()), data._source.bibleRefs)
                    ArticleViewContent(
                        article,
                        markupId,
                        markups,
                        signInModel,
                        favoritesModel,
                        markupsModel
                    )
                }
            }
            Status.LOADING -> {}
            Status.ERROR -> {}
        }
    }
    LaunchedEffect(Unit) {
        articleModel.getArticle(articleID, query)
    }
}

