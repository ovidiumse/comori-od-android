package com.ovidium.comoriod.views.search.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.ovidium.comoriod.data.search.Aggregations
import com.ovidium.comoriod.ui.theme.getNamedColor

enum class SearchSource {
    SEARCH, AUTHOR
}


@Composable
fun SearchFilterPopup(
    aggregations: Aggregations?,
    searchSource: SearchSource,
    onCheck: (FilterCategory, String) -> Unit,
    onSaveAction: () -> Unit,
    onExitAction: () -> Unit
) {
    Popup(
        alignment = Alignment.BottomCenter,
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        val isDark = isSystemInDarkTheme()
        Box(
            Modifier
                .size(screenWidth, screenHeight)
                .background(
                    getNamedColor("Container", isDark = isDark),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column() {
                FilterViewTopBar(onSaveAction = onSaveAction, onExitAction = onExitAction)
                LazyColumn(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (searchSource == SearchSource.SEARCH) {
                        item {
                            FilterCategoryView(
                                category = FilterCategory.TYPES,
                                buckets = aggregations?.types!!.buckets,
                                onCheck = onCheck,
                                isDark = isDark
                            )
                        }
                        item {
                            FilterCategoryView(
                                category = FilterCategory.AUTHORS,
                                buckets = aggregations?.authors!!.buckets,
                                onCheck = onCheck,
                                isDark = isDark
                            )
                        }
                    }
                    item {
                        FilterCategoryView(
                            category = FilterCategory.VOLUMES,
                            buckets = aggregations?.volumes!!.buckets,
                            onCheck = onCheck,
                            isDark = isDark
                        )
                    }
                    item {
                        FilterCategoryView(
                            category = FilterCategory.BOOKS,
                            buckets = aggregations?.books!!.buckets,
                            onCheck = onCheck,
                            isDark = isDark
                        )
                    }
                }
            }
        }
    }
}
