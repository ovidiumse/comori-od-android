package com.ovidium.comoriod.views.library

import androidx.compose.foundation.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.material.placeholder
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.Screens

@Composable
fun ItemCard(
    navController: NavController,
    title: String,
    id: String,
    itemType: ItemCategory,
    isLoading: Boolean,
    secondary: String? = null,
    detail: String? = null,
    imageId: Int? = null,
    colors: List<Color>,
    itemSize: Int,
    marginSize: Int,
    isDark: Boolean
) {
    val titleAreaSize = itemSize * 0.60
    val authorBarSize = itemSize * 0.18
    val authorImageSize = authorBarSize * 1.60
    val authorImageLeftOffset = authorBarSize * 0.3

    val darkBarColor = getNamedColor("DarkBar", isDark)
    val secondaryBackground = getNamedColor("SecondaryBackground", isDark)
    val secondarySurfaceColor = getNamedColor("SecondarySurface", isDark)

    Card(shape = RoundedCornerShape(12)) {
        var boxModifier = Modifier
            .size(width = itemSize.dp, height = itemSize.dp)
            .placeholder(
                isLoading,
                color = secondaryBackground,
                highlight = PlaceholderHighlight.fade(highlightColor = secondarySurfaceColor)
            )
            .clickable {
                when (itemType) {
                    ItemCategory.Volume -> {
                        navController.navigate(Screens.BooksForVolume.withArgs(title))
                    }
                    ItemCategory.Book -> {
                        navController.navigate(Screens.Book.withArgs(title))
                    }
                    ItemCategory.Article -> {
                        navController.navigate(Screens.Article.withArgs(id))
                    }
                    else -> {}
                }
            }
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
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
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
                        .background(darkBarColor)
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
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .align(Alignment.CenterVertically),
                            color = darkBarColor
                        )
                    }
                }
            }
        }
    }
}
