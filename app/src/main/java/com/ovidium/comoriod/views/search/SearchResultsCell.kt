package com.ovidium.comoriod.views.search

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ovidium.comoriod.components.TextCard
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.*
import com.ovidium.comoriod.views.Screens
import java.net.URLEncoder

@Composable
fun SearchResultsCell(
    index: Int,
    hit: Hit,
    navController: NavController,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = getNamedColor("Container", isDark)
    val mutedText = getNamedColor("MutedText", isDark)
    val bubbleColor = getNamedColor("PrimarySurface", isDark)

    fun getTitle(hit: Hit): String {
        return if (!hit.highlight?.title.isNullOrEmpty()) {
            hit.highlight!!.title!![0]
        } else hit._source.title
    }

    TextCard(
        index = index,
        title = highlightElements(getTitle(hit), isDark),
        book = hit._source.book,
        author = hit._source.author,
        type = hit._source.type,
        lines = fmtVerses(hit.highlight?.body.orEmpty(), isDark = isSystemInDarkTheme()),
        subtitleColor = mutedText,
        bgColor = bgColor,
        bubbleColor = bubbleColor,
        modifier = modifier
    ) {
        navController.navigate(
            Screens.Article.withArgs("${URLEncoder.encode(hit._id, "utf-8")}?isSearch=true")
        ) {
            launchSingleTop = true
        }
    }
}
