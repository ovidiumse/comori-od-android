package com.ovidium.comoriod.views.search

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
import com.ovidium.comoriod.views.search.filter.FilterCategory
import com.ovidium.comoriod.views.search.filter.FilterCategoryView
import com.ovidium.comoriod.views.search.filter.FilterViewTopBar

@Composable
fun SearchFilterPopup(aggregations: Aggregations?, onSaveAction: () -> Unit, onExitAction: () -> Unit) {
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
                    getNamedColor("Container", isDark = isDark)!!,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column() {
                FilterViewTopBar(onSaveAction = onSaveAction, onExitAction = onExitAction)
                LazyColumn(
                    modifier = Modifier.padding(16.dp)
                ) {
                    item {
                        FilterCategoryView(category = FilterCategory.TYPES, buckets = aggregations?.types!!.buckets, isDark = isDark)
                    }
                    item {
                        FilterCategoryView(category = FilterCategory.AUTHORS, buckets = aggregations?.authors!!.buckets, isDark = isDark)
                    }
                    item {
                        FilterCategoryView(category = FilterCategory.VOLUMES, buckets = aggregations?.volumes!!.buckets, isDark = isDark)
                    }
                    item {
                        FilterCategoryView(category = FilterCategory.BOOKS, buckets = aggregations?.books!!.buckets, isDark = isDark)
                    }
                }
            }
        }
    }
}
