package com.ovidium.comoriod.views.search

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.TextCard
import com.ovidium.comoriod.data.search.Hit
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.fmtVerses
import com.ovidium.comoriod.utils.highlightText
import com.ovidium.comoriod.views.Screens
import java.net.URLEncoder

@Composable
fun SearchResultsCell(
    index: Int,
    hit: Hit,
    navController: NavController,
    isDark: Boolean
) {
    val bgColor = getNamedColor("PrimarySurface", isDark)
    val mutedText = getNamedColor("MutedText", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)

    fun getTitle(hit: Hit): String {
        return if (!hit.highlight.title.isNullOrEmpty()) {
            hit.highlight.title[0]
        } else hit._source.title
    }

    TextCard(
        index = index,
        title = highlightText(getTitle(hit), isDark),
        book = hit._source.book,
        author = hit._source.author,
        type = hit._source.type,
        text = fmtVerses(hit.highlight.versesText.orEmpty(), isDark = isSystemInDarkTheme()),
        subtitleColor = mutedText,
        bgColor = bgColor,
        bubbleColor = bubbleColor
    ) {
        navController.navigate(
            Screens.Article.withArgs(
                URLEncoder.encode(
                    hit._id,
                    "utf-8"
                )
            )
        ) {
            launchSingleTop = true
        }
    }
}
