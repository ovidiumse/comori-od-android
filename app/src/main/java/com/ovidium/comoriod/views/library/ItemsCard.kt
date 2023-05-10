package com.ovidium.comoriod.views.library

import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.material.placeholder
import com.ovidium.comoriod.components.AdaptiveText
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.Screens
import java.net.URLEncoder

@Composable
fun ItemCard(
    navController: NavController,
    title: String,
    id: String,
    itemType: ItemCategory,
    isLoading: Boolean,
    secondary: String? = null,
    detail: String? = null,
    image_url: String? = null,
    colors: List<Color>,
    itemSize: Int,
    marginSize: Int,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val titleAreaSize = itemSize * 0.60
    val authorBarSize = itemSize * 0.18
    val authorImageSize = authorBarSize * 1.60
    val authorImageLeftOffset = authorBarSize * 0.3

    val darkBarColor = getNamedColor("DarkBar", isDark)
    val secondaryBackground = getNamedColor("SecondaryBackground", isDark)
    val secondarySurfaceColor = getNamedColor("SecondarySurface", isDark)

    Card(shape = RoundedCornerShape(12), modifier = modifier) {
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
                        if (title.isNotEmpty())
                            navController.navigate(Screens.BooksForVolume.withArgs(URLEncoder.encode(title, "utf-8")))
                    }

                    ItemCategory.Book -> {
                        if (title.isNotEmpty())
                            navController.navigate(Screens.Book.withArgs(URLEncoder.encode(title, "utf-8")))
                    }

                    ItemCategory.Article -> {
                        if (id.isNotEmpty())
                            navController.navigate(Screens.Article.withArgs(URLEncoder.encode(id, "utf-8")))
                    }

                    else -> {}
                }
            }
        if (!isLoading)
            boxModifier = boxModifier.background(brush = Brush.verticalGradient(colors))

        Box(boxModifier) {
            Column {
                Row(
                    modifier = Modifier.height(titleAreaSize.dp)
                ) {
                    AdaptiveText(
                        text = title,
                        minFontSize = 14.sp,
                        maxFontSize = 28.sp,
                        color = darkBarColor,
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
                        image_url?.let {
                            Spacer(modifier = Modifier.width(authorImageLeftOffset.dp))

                            Image(
                                painter = rememberAsyncImagePainter(image_url),
                                contentDescription = "details",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .requiredSize(authorImageSize.dp)
                                    .clip(RoundedCornerShape(100))
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        secondary?.let {
                            AdaptiveText(
                                text = secondary,
                                minFontSize = 8.sp,
                                maxFontSize = 16.sp,
                                style = MaterialTheme.typography.caption,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .align(Alignment.CenterVertically),
                                color = Color.White
                            )
                        }
                    }
                }

                detail?.let {
                    Row(modifier = Modifier.fillMaxHeight()) {
                        AdaptiveText(
                            text = detail,
                            minFontSize = 8.sp,
                            maxFontSize = 16.sp,
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
