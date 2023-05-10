package com.ovidium.comoriod.views.search.filter

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.AdaptiveText
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun SearchFilterPopup(
    aggregations: SnapshotStateMap<Int, SearchModel.Aggregation>,
    params: SnapshotStateMap<String, MutableSet<String>>,
    onCheck: (String, String, Boolean) -> Unit,
    onSaveAction: () -> Unit,
    onExitAction: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) getNamedColor("Background", isDark) else getNamedColor("Container", isDark)
    val buttonColor = getNamedColor("Button", isDark)
    val textColor = getNamedColor("Text", isDark)

    Popup(
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        Box(
            Modifier
                .size(screenWidth, screenHeight)
                .background(bgColor, shape = RectangleShape)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(0.33f))
                    AdaptiveText(
                        text = "Filtrează",
                        minFontSize = 14.sp,
                        maxFontSize = 28.sp,
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.33f)
                    )
                    Column(modifier = Modifier.weight(0.33f), horizontalAlignment = Alignment.End) {
                        Icon(
                            modifier = Modifier.clickable { onExitAction() },
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_close_24),
                            tint = textColor,
                            contentDescription = "Închide"
                        )
                    }
                }

                LazyColumn(modifier = Modifier.padding(horizontal = 12.dp)) {
                    itemsIndexed(
                        aggregations.toList().map { (_, agg) -> agg }) { index, aggregation ->

                        FilterCategoryView(
                            category = aggregation.name,
                            paramName = aggregation.paramName,
                            buckets = aggregation.buckets,
                            params = params.getOrDefault(aggregation.paramName, emptySet()),
                            onCheck = onCheck,
                            isDark = isDark
                        )

                        if (index == aggregations.count() - 1)
                            Spacer(modifier = Modifier.height(54.dp))
                        else
                            Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Spacer(modifier = Modifier.weight(0.33f))
                    FloatingActionButton(
                        modifier = Modifier
                            .weight(0.33f)
                            .height(34.dp),
                        shape = MaterialTheme.shapes.small,
                        onClick = onSaveAction,
                        backgroundColor = buttonColor
                    ) {
                        AdaptiveText(
                            text = "Aplică",
                            minFontSize = 8.sp,
                            maxFontSize = 20.sp,
                            style = MaterialTheme.typography.button,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.33f))
                }
            }
        }
    }
}
