package com.ovidium.comoriod.views.search.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun SimpleCheckbox(
    modifier: Modifier = Modifier,
    paramName: String,
    item: String,
    checked: Boolean,
    onCheck: (String, String, Boolean) -> Unit
) {
    Checkbox(
        checked = checked,
        onCheckedChange = { checked ->
            onCheck(paramName, item, checked)
        },
        modifier = modifier
            .size(24.dp)
            .padding(end = 8.dp, start = 16.dp)
    )
}

@Composable
fun FilterCategoryView(
    category: String,
    paramName: String,
    buckets: List<SearchModel.AggregationBucket>,
    params: Set<String>,
    onCheck: (String, String, Boolean) -> Unit,
    isDark: Boolean
) {
    val bgColor = if (isDark) getNamedColor("Container", isDark) else getNamedColor("Background", isDark)

    Column(
        modifier = Modifier
            .background(bgColor, shape = Shapes.small)
    ) {
        Text(
            text = category,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp)
        )
        buckets.forEach { item ->
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SimpleCheckbox(
                    modifier = Modifier.weight(0.1f),
                    paramName,
                    item.key,
                    params.contains(item.key),
                    onCheck
                )

                Text(
                    text = item.key,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .padding(start = 8.dp)
                        .clickable { onCheck(paramName, item.key, !params.contains(item.key)) }
                )

                Text(
                    text = item.doc_count.toString(),
                    textAlign = TextAlign.End,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f)
                        .padding(end = 12.dp)
                        .clickable { onCheck(paramName, item.key, !params.contains(item.key)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

