package com.ovidium.comoriod.views.search.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.data.search.Bucket
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.getNamedColor

enum class FilterCategory(val value: String) {
    TYPES("types"),
    AUTHORS("authors"),
    VOLUMES("volumes"),
    BOOKS("books");

    fun localizedTitle(): String {
        when (this) {
            TYPES -> { return "Tip" }
            AUTHORS -> { return "Autori" }
            VOLUMES -> { return "Volume" }
            BOOKS -> { return "Cărți" }
        }
    }
}


@Composable
fun FilterCategoryView(category: FilterCategory, buckets: List<Bucket>, onCheck: (FilterCategory, String) -> Unit, isDark: Boolean) {

    val selected = remember { mutableStateListOf<String>() }

    Column(modifier= Modifier.padding(bottom = 30.dp)) {
        Column(
            modifier = Modifier
                .background(
                    getNamedColor("InvertedText", isDark = isDark),
                    shape = Shapes.small
                )
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = category.localizedTitle(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
            )
            buckets.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selected.contains(item.key),
                        onCheckedChange = {
                            onCheck(category, item.key)
                            if (selected.contains(item.key)) {
                                selected.remove(item.key)
                            } else {
                                selected.add(item.key)
                            }
                                          },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.1f)
                            .padding(end = 8.dp, start = 16.dp)
                    )
                    Text(
                        text = item.key,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Light
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.6f)
                            .padding(start = 8.dp)
                    )
                    Text(
                        text = item.docCount.toString(),
                        textAlign = TextAlign.End,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Light
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.3f)
                            .padding(end = 16.dp)
                    )
                }
            }
        }
    }
}