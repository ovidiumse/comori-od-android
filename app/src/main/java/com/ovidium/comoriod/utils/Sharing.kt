package com.ovidium.comoriod.utils

import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.ovidium.comoriod.data.article.Article

fun shareSelection(context: Context, author: String, book: String, articleId: String, selection: String, index: Int, length: Int) {
    val sharingData = """„${selection}”
$author - $book
https://comori-od.ro/article/${articleId}?index=${index}&length=${length}"""

    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, sharingData)
    ContextCompat.startActivity(context, Intent.createChooser(shareIntent, null), null)
}

fun shareArticle(context: Context, article: Article) {
    val sharingData = """${article.title} - ${article.author}
https://comori-od.ro/article/${article.id}"""

    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, sharingData)
    ContextCompat.startActivity(context, Intent.createChooser(shareIntent, null), null)
}