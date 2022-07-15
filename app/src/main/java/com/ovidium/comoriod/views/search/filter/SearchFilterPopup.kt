package com.ovidium.comoriod.views.search.filter

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun SearchFilterPopup(
    aggregations: SnapshotStateMap<Int, SearchModel.Aggregation>,
    params: SnapshotStateMap<String, MutableSet<String>>,
    onCheck: (String, String, Boolean) -> Unit,
    onSaveAction: () -> Unit,
    onExitAction: () -> Unit
) {
    val bgColor = getNamedColor("PrimarySurface", isDark = isSystemInDarkTheme())

    Popup(
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        val isDark = isSystemInDarkTheme()
        Box(
            Modifier
                .size(screenWidth, screenHeight)
                .background(bgColor, shape = RectangleShape)
        ) {
            Column {
                FilterViewTopBar(onSaveAction = onSaveAction, onExitAction = onExitAction)
                LazyColumn(Modifier.padding(12.dp)) {
                    itemsIndexed(
                        aggregations.toList().map { (_, agg) -> agg }) { index, aggregation ->
                        Spacer(modifier = Modifier.height(12.dp))

                        FilterCategoryView(
                            category = aggregation.name,
                            paramName = aggregation.paramName,
                            buckets = aggregation.buckets,
                            params = params.getOrDefault(aggregation.paramName, emptySet()),
                            onCheck = onCheck,
                            isDark = isDark
                        )

                        if (index == aggregations.count() - 1)
                            Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
