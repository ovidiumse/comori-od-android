package com.ovidium.comoriod.views.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.views.DataItem
import com.ovidium.comoriod.views.ItemCategory

@Composable
fun HorizontalCarousel(
    navController: NavController,
    name: String,
    dataItems: List<DataItem>?,
    estimatedSize: Int,
    isLoading: Boolean,
    showAuthorAction: (Bucket?) -> Unit
) {
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
            name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
            if (isLoading)
                repeat(estimatedSize) {
                    item() {
                        ItemCard(
                            navController = navController,
                            title = "",
                            itemType = ItemCategory.None,
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
                        ItemCard(
                            navController,
                            dataItem.title,
                            dataItem.type,
                            isLoading,
                            dataItem.secondary,
                            dataItem.detail,
                            dataItem.imageId,
                            dataItem.gradient,
                            itemSize,
                            marginSize,
                            showAuthorAction = showAuthorAction
                        )
                    }

                }
            }
        }
    }
}