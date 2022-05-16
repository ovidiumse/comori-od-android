package com.ovidium.comoriod.views.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.articulate
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem
import com.ovidium.comoriod.views.ItemCategory
import kotlin.math.ceil

@Composable
fun AuthorsGrid(
    navController: NavController,
    response: AuthorsResponse?,
    isLoading: Boolean,
    isDark: Boolean,
    showAuthorAction: (Bucket?) -> Unit
) {

    val itemMinWidth = 180
    val marginSize = 12
    val itemsByRow = LocalConfiguration.current.screenWidthDp / itemMinWidth
    val itemSize =
        (LocalConfiguration.current.screenWidthDp - (itemsByRow + 1) * marginSize) / itemsByRow
    val estimatedSize = 2

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
                            AuthorCard(
                                authorInfo = null,
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
            response?.aggregations?.authors?.buckets?.let { buckets ->
                items(buckets.chunked(itemsByRow)) { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(marginSize.dp)) {
                        for (item in rowItems) {
                            AuthorCard(
                                item,
                                isLoading,
                                getVolumeCoverGradient("", isDark = isDark),
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
}