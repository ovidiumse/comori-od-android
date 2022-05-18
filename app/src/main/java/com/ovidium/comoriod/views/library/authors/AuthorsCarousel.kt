package com.ovidium.comoriod.views.library

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem

@Composable
fun AuthorsCarousel(
    title: String,
    dataItems: List<Bucket>?,
    estimatedSize: Int,
    isLoading: Boolean,
    showAuthorAction: (Bucket?) -> Unit
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
        modifier = Modifier
            .fillMaxHeight()
            .padding(marginSize.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = getNamedColor("OnBackground", isDark)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
            if (isLoading)
                repeat(estimatedSize) {
                    item() {
                        AuthorCard(
                            null,
                            isLoading = isLoading,
                            colors = emptyList(),
                            itemSize = itemSize,
                            marginSize = marginSize,
                            showAuthorAction = showAuthorAction
                        )
                    }
                }
            else {
                dataItems?.let {
                    items(dataItems) { dataItem ->
                        AuthorCard(
                            dataItem,
                            isLoading,
                            getVolumeCoverGradient("", isSystemInDarkTheme()),
                            itemSize,
                            marginSize,
                            showAuthorAction
                        )
                    }

                }
            }
        }
    }
}