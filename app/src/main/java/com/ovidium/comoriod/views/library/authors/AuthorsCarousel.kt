package com.ovidium.comoriod.views.library

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.components.AdaptiveText
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.getAuthorCoverGradient
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.library.authors.AuthorCard

@Composable
fun AuthorsCarousel(
    title: String,
    dataItems: List<Bucket>?,
    estimatedSize: Int,
    isLoading: Boolean,
    showAuthorAction: (Bucket?) -> Unit,
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
            .padding(top = marginSize.dp)
    ) {
        AdaptiveText(
            title,
            minFontSize = 16.8.sp,
            maxFontSize = 30.sp,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = getNamedColor("OnBackground", isDark),
            modifier = Modifier.padding(start = marginSize.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
            if (isLoading)
                repeat(estimatedSize) { index ->
                    item {
                        if (index == 0)
                            Spacer(modifier = Modifier.width(marginSize.dp))

                        AuthorCard(
                            null,
                            isLoading = isLoading,
                            colors = emptyList(),
                            itemSize = itemSize,
                            marginSize = marginSize,
                            isDark = isDark,
                            showAuthorAction = showAuthorAction,
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

                        AuthorCard(
                            dataItem,
                            isLoading,
                            getAuthorCoverGradient(dataItem.name, isSystemInDarkTheme()),
                            itemSize,
                            marginSize,
                            isDark,
                            showAuthorAction,
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