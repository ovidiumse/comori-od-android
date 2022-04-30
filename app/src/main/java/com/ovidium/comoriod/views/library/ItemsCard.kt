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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.material.placeholder
import com.ovidium.comoriod.views.Screens

@Composable
fun ItemCard(
    navController: NavController,
    title: String,
    isLoading: Boolean,
    secondary: String? = null,
    detail: String? = null,
    imageId: Int? = null,
    colors: List<Color>,
    itemSize: Int,
    marginSize: Int
) {
    val titleAreaSize = itemSize * 0.60
    val authorBarSize = itemSize * 0.18
    val authorImageSize = authorBarSize * 1.60
    val authorImageLeftOffset = authorBarSize * 0.3

    Card(Modifier.clip(RoundedCornerShape(8))) {
        var boxModifier = Modifier
            .size(width = itemSize.dp, height = itemSize.dp)
            .placeholder(
                isLoading,
                color = Color.DarkGray,
                highlight = PlaceholderHighlight.fade(highlightColor = Color.LightGray)
            )
            .clickable { navController.navigate(Screens.Book.withArgs(title)) }
        if (!isLoading)
            boxModifier = boxModifier.background(brush = Brush.verticalGradient(colors))

        Box(boxModifier) {
            Column {
                Row(
                    modifier = Modifier
                        .height(titleAreaSize.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth()
                            .padding(horizontal = marginSize.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color.DarkGray)
                        .height(authorBarSize.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        imageId?.let {
                            Image(
                                painter = painterResource(imageId),
                                contentDescription = "details",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .offset(x = authorImageLeftOffset.dp)
                                    .requiredSize(authorImageSize.dp)
                                    .clip(RoundedCornerShape(100))
                            )
                        }

                        secondary?.let {
                            Text(
                                text = secondary,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically),
                                color = Color.White
                            )
                        }
                    }
                }

                detail?.let {
                    Row(modifier = Modifier.fillMaxHeight()) {
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.caption,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}