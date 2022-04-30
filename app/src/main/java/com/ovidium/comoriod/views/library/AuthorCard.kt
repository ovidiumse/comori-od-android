package com.ovidium.comoriod.views.library

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

@Composable
fun AuthorCard(
    title: String,
    isLoading: Boolean,
    imageId: Int? = null,
    colors: List<Color>,
    itemSize: Int,
    marginSize: Int
) {
    val imageAreaSize = itemSize * 0.80
    val authorImageSize = imageAreaSize * 0.80

    Card(Modifier.clip(RoundedCornerShape(8))) {
        var boxModifier = Modifier
            .size(width = itemSize.dp, height = itemSize.dp)
            .placeholder(
                isLoading,
                color = Color.DarkGray,
                highlight = PlaceholderHighlight.fade(highlightColor = Color.LightGray)
            )
            .clickable { println("Author clicked: $title") }
        if (!isLoading)
            boxModifier = boxModifier.background(brush = Brush.verticalGradient(colors))

        Box(boxModifier) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(20.dp))
                imageId?.let {
                    Image(
                        painter = painterResource(imageId),
                        contentDescription = "details",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .requiredSize(authorImageSize.dp)
                            .clip(RoundedCornerShape(100))
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
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