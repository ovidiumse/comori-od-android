package com.ovidium.comoriod.views.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.utils.articulate
import com.ovidium.comoriod.views.DataItem
import com.ovidium.comoriod.views.ItemCategory
import kotlin.math.ceil

@Composable
fun ItemsGrid(
    navController: NavController,
    names: Pair<String, String>,
    items: List<DataItem>?,
    estimatedSize: Int,
    isLoading: Boolean = false,
    showAuthorAction: (Bucket?) -> Unit
) {
    val itemMinWidth = 180
    val marginSize = 12
    val itemsByRow = LocalConfiguration.current.screenWidthDp / itemMinWidth
    val itemSize =
        (LocalConfiguration.current.screenWidthDp - (itemsByRow + 1) * marginSize) / itemsByRow

    LazyColumn(
        contentPadding = PaddingValues(horizontal = marginSize.dp, vertical = marginSize.dp),
        verticalArrangement = Arrangement.spacedBy(marginSize.dp),
        modifier = Modifier
            .fillMaxHeight()
    ) {
        if (isLoading) {
            repeat(ceil(estimatedSize.toDouble() / itemsByRow).toInt()) {
                item() {
                    Row(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
                        repeat(itemsByRow) {
                            ItemCard(
                                navController,
                                title = "",
                                itemType = ItemCategory.Article,
                                isLoading,
                                itemSize = itemSize,
                                colors = emptyList(),
                                marginSize = marginSize,
                                showAuthorAction = showAuthorAction
                            )
                        }

                    }
                }
            }
        } else {
            items?.let {
                fun renderItems(items: List<DataItem>) {
                    items(items.chunked(itemsByRow)) { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
                            for (item in rowItems) {
                                ItemCard(
                                    navController,
                                    item.title,
                                    item.type,
                                    isLoading = isLoading,
                                    item.secondary,
                                    item.detail,
                                    item.imageId,
                                    item.gradient,
                                    itemSize,
                                    marginSize,
                                    showAuthorAction = showAuthorAction
                                )
                            }
                        }
                    }
                }

                val hasDetail = items.isNotEmpty() && items[0].detail != null
                val hasSecondary = items.isNotEmpty() && items[0].secondary != null

                if (hasDetail || hasSecondary) {
                    val grouped = items.groupBy { item -> item.detail ?: item.secondary }

                    grouped.forEach { (group, items) ->
                        stickyHeader {
                            Card(
                                modifier = Modifier
                                    .offset(x = 1.dp, y = 1.dp)
                                    .wrapContentWidth()
                                    .clip(RoundedCornerShape(30))
                            ) {
                                Text(
                                    group.toString() + " - " + articulate(
                                        items.size,
                                        names.second,
                                        names.first
                                    ),
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }

                        renderItems(items)
                    }
                } else
                    renderItems(items)
            }
        }
    }
}