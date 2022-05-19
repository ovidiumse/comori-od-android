package com.ovidium.comoriod.views.article

import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.model.ArticleModel
import com.ovidium.comoriod.model.BookModel
import com.ovidium.comoriod.model.FavoritesModel
import com.ovidium.comoriod.model.MarkupsModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.parseVerses
import com.ovidium.comoriod.views.favorites.SaveFavoriteDialog
import com.ovidium.comoriod.views.markups.SaveMarkupDialog

@Composable
fun ArticleView(articleID: String, favoritesModel: FavoritesModel, markupsModel: MarkupsModel, clipboardManager: ClipboardManager) {

    val articleModel: BookModel = viewModel()
    val bookData = remember { articleModel.bookData }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val articleData = bookData.getOrDefault(articleID, Resource.loading(data = null))
        when (articleData.status) {
            Status.SUCCESS -> {
                articleData.data?.let { article ->
                    ArticleViewContent(article, favoritesModel, markupsModel, clipboardManager)
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

fun Context.findActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun ArticleViewContent(article: ArticleResponse, favoritesModel: FavoritesModel, markupsModel: MarkupsModel, clipboardManager: ClipboardManager) {
    val articleModel: ArticleModel = viewModel()
    val bibleRefs = articleModel.getBibleRefs(article._id)
    var showSaveFavoriteDialog by remember { mutableStateOf(false) }
    var markupSelection = remember { mutableStateOf("") }
    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box() {
        Column(
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
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
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
        ) {
                FloatingActionButton(
                    onClick = {
                        if (favoritesModel.isFavorite(article._id)) {
                            showDeleteFavoriteDialog = true
                        } else {
                            showSaveFavoriteDialog = true
                        }
                    },
                    modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                    backgroundColor = if (favoritesModel.isFavorite(article._id)
                    ) Color.Red else getNamedColor("Link", isSystemInDarkTheme())!!
                ) {
                    if (favoritesModel.isFavorite(article._id)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_delete_24),
                            contentDescription = "Delete",
                            tint = getNamedColor("Container", isSystemInDarkTheme())!!,
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_star_24),
                            contentDescription = "Favorite",
                            tint = getNamedColor("Container", isSystemInDarkTheme())!!,
                        )
                    }
                }
        }
    }
    if (showSaveFavoriteDialog) {
        SaveFavoriteDialog(
            articleToSave = article,
            onSaveAction = { tags ->
                val favoriteArticle = FavoriteArticle(
                    id = article._id,
                    title = article.title,
                    tags = tags,
                    author = article.author,
                    book = article.book,
                    timestamp = ""
                )
                favoritesModel.saveFavorite(favoriteArticle)
                showSaveFavoriteDialog = false
            },
            onExitAction = {
                showSaveFavoriteDialog = false
            }
        )
    }

    if (markupSelection.value.isNotEmpty()) {
        SaveMarkupDialog(
            articleToSave = article,
            selection = markupSelection.value,
            onSaveAction = { markup ->
                markupsModel.save(markup)
                markupSelection.value = ""
            },
            onExitAction = {
                markupSelection.value = ""
            }
        )
    }

    if (showDeleteFavoriteDialog) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(text = "Atenție")
            },
            text = {
                Text("Ești sigur că vrei să ștergi acest articol favorit?")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    ),
                    onClick = {
                        favoritesModel.deleteFavoriteArticle(article._id)
                        showDeleteFavoriteDialog = false
                    }) {
                    Text("Șterge")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = getNamedColor("Link", isSystemInDarkTheme())!!,
                        contentColor = Color.White
                    ),
                    onClick = {
                        showDeleteFavoriteDialog = false
                    }) {
                    Text("Renunță")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        // Set clipboard primary clip change listener
        clipboardManager.addPrimaryClipChangedListener {
            val text: String = clipboardManager.primaryClip?.getItemAt(0)?.text.toString().trim()
//            val activity = context.findActivity()
//            val text = activity?.intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
            markupSelection.value = text
        }
    }

}

