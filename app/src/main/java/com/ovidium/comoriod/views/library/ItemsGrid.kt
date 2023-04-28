package com.ovidium.comoriod.views.library

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.articulate
import kotlin.math.ceil

@Composable
fun ItemsGrid(
    navController: NavController,
    names: Pair<String, String>,
    items: List<DataItem>?,
    estimatedSize: Int,
    isLoading: Boolean = false,
) {
    val isDark = isSystemInDarkTheme()

    val itemMinWidth = 180
    val marginSize = 12
    val itemsByRow = LocalConfiguration.current.screenWidthDp / itemMinWidth
    val itemSize =
        (LocalConfiguration.current.screenWidthDp - (itemsByRow + 1) * marginSize) / itemsByRow

    val backgroundColor = getNamedColor("Background", isDark)
    val borderColor = getNamedColor("Border", isDark)
    val textColor = getNamedColor("OnBackground", isDark)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = marginSize.dp, vertical = marginSize.dp),
        verticalArrangement = Arrangement.spacedBy(marginSize.dp),
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxHeight()
    ) {
        if (isLoading) {
            repeat(ceil(estimatedSize.toDouble() / itemsByRow).toInt()) { mainIndex ->
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(marginSize.dp),
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(
                            animationSpec = tween(durationMillis = 300)
                        )
                    ) {
                        repeat(itemsByRow) {
                            ItemCard(
                                navController,
                                title = "",
                                id = "",
                                itemType = ItemCategory.Article,
                                isLoading,
                                itemSize = itemSize,
                                colors = emptyList(),
                                marginSize = marginSize,
                                isDark = isDark,
                                modifier = Modifier.animateItemPlacement(
                                    animationSpec = tween(durationMillis = 300)
                                )
                            )
                        }
                    }
                }
            }
        } else {
            items?.let {
                fun renderItems(items: List<DataItem>) {
                    items(items.chunked(itemsByRow)) { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(marginSize.dp),
                            modifier = Modifier.fillMaxWidth().animateItemPlacement(
                                animationSpec = tween(durationMillis = 300)
                            )
                        ) {
                            for (item in rowItems) {
                                ItemCard(
                                    navController,
                                    item.title,
                                    item.id,
                                    item.type,
                                    isLoading,
                                    item.secondary,
                                    item.detail,
                                    item.image_url,
                                    item.gradient,
                                    itemSize,
                                    marginSize,
                                    isDark,
                                    modifier = Modifier.animateItemPlacement(
                                        animationSpec = tween(durationMillis = 300)
                                    )
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
                            val shape = RoundedCornerShape(30)
                            Card(
                                backgroundColor = backgroundColor,
                                elevation = 0.dp,
                                shape = shape,
                                modifier = Modifier
                                    .offset(x = 1.dp, y = 1.dp)
                                    .wrapContentWidth()
                                    .border(1.dp, borderColor, shape)
                            ) {
                                Text(
                                    group.toString() + " - " + articulate(
                                        items.size,
                                        names.second,
                                        names.first
                                    ),
                                    style = MaterialTheme.typography.h6,
                                    color = textColor,
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