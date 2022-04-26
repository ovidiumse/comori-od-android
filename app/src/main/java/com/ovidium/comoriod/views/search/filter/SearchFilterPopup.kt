package com.ovidium.comoriod.views.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.data.search.Aggregations
import com.ovidium.comoriod.data.search.Bucket
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.formatBibleRefs
import com.ovidium.comoriod.views.search.filter.FilterCategory
import com.ovidium.comoriod.views.search.filter.FilterViewTopBar
import java.util.*

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
                        FilterCategory(buckets = aggregations?.types!!.buckets, title = "Tip", isDark = isDark)
                    }
                    item {
                        FilterCategory(buckets = aggregations?.authors!!.buckets, title = "Autori", isDark = isDark)
                    }
                    item {
                        FilterCategory(buckets = aggregations?.volumes!!.buckets, title = "Volume", isDark = isDark)
                    }
                    item {
                        FilterCategory(buckets = aggregations?.books!!.buckets, title = "Carti", isDark = isDark)
                    }
                }
            }
        }
    }
}
