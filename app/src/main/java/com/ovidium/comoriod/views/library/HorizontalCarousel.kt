package com.ovidium.comoriod.views.library

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun HorizontalCarousel(
    navController: NavController,
    name: String,
    dataItems: List<DataItem>?,
    estimatedSize: Int,
    isLoading: Boolean,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val itemMinWidth = 180
    val marginSize = 12
    var screenWidth = LocalConfiguration.current.screenWidthDp
    val itemsByRow = screenWidth / itemMinWidth

    fun calculateItemSize(screenWidth: Int): Int {
        return (screenWidth - (itemsByRow + 1) * marginSize) / itemsByRow
    }

    var itemSize = calculateItemSize(screenWidth)
    val itemCnt = dataItems?.size ?: estimatedSize
    if (itemCnt > itemsByRow) {
        screenWidth -= (itemSize * 0.3).toInt()
        itemSize = calculateItemSize(screenWidth)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(marginSize.dp),
        modifier = modifier
            .fillMaxHeight()
            .padding(
                top = marginSize.dp,
                bottom = if (isLast) marginSize.dp else 0.dp
            )
    ) {
        Text(
            name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = getNamedColor("OnBackground", isDark),
            modifier = Modifier.padding(start = marginSize.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(marginSize.dp),
        modifier = Modifier
        ) {
            if (isLoading)
                repeat(estimatedSize) { index ->
                    item {
                        if (index == 0)
                            Spacer(modifier = Modifier.width(marginSize.dp))

                        ItemCard(
                            navController = navController,
                            title = "",
                            id = "",
                            itemType = ItemCategory.None,
                            isLoading = isLoading,
                            colors = emptyList(),
                            itemSize = itemSize,
                            marginSize = marginSize,
                            isDark = isDark,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(durationMillis = 300)
                            )
                        )

                        if (index == estimatedSize - 1)
                            Spacer(modifier = Modifier.width(marginSize.dp))
                    }
                }
            else {
                dataItems?.let {
                    itemsIndexed(dataItems) { index, dataItem ->
                        if (index == 0)
                            Spacer(modifier = Modifier.width(marginSize.dp))

                        ItemCard(
                            navController,
                            dataItem.title,
                            dataItem.id,
                            dataItem.type,
                            isLoading,
                            dataItem.secondary,
                            dataItem.detail,
                            dataItem.image_url,
                            dataItem.gradient,
                            itemSize,
                            marginSize,
                            isDark,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(durationMillis = 300)
                            )
                        )

                        if (index == dataItems.lastIndex)
                            Spacer(modifier = Modifier.width(marginSize.dp))
                    }
                }
            }
        }
    }
}