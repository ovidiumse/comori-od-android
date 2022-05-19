package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.material.placeholder
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.ItemCategory

@Composable
fun AuthorCard(
    authorInfo: Bucket?,
    isLoading: Boolean,
    colors: List<Color>,
    itemSize: Int,
    marginSize: Int,
    isDark: Boolean,
    showAuthorAction: (Bucket?) -> Unit
) {
    val imageAreaSize = itemSize * 0.80
    val authorImageSize = imageAreaSize * 0.80

    val secondaryBackground = getNamedColor("SecondaryBackground", isDark)
    val secondarySurface = getNamedColor("SecondarySurface", isDark)

    Card(shape = RoundedCornerShape(12)) {
        var boxModifier = Modifier
            .size(width = itemSize.dp, height = itemSize.dp)
            .placeholder(
                isLoading,
                color = secondaryBackground,
                highlight = PlaceholderHighlight.fade(highlightColor = secondarySurface)
            )
            .clickable { showAuthorAction(authorInfo) }
        if (!isLoading)
            boxModifier = boxModifier.background(brush = Brush.verticalGradient(colors))

        Box(boxModifier) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(getDrawableByAuthor(authorInfo?.name ?: "")),
                    contentDescription = "details",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .requiredSize(authorImageSize.dp)
                        .clip(RoundedCornerShape(100))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = authorInfo?.name ?: "",
                    color = Color.Black,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(horizontal = marginSize.dp)
                )
            }
        }
    }
}