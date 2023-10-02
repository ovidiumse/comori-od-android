package com.ovidium.comoriod.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun TagsRow(
    tags: List<String>,
    tagCounts: Map<String, Int>,
    count: Int,
    selectedTag: String,
    onTagsChanged: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    LazyRow(
        modifier = Modifier
            .padding(12.dp)
    ) {
        item {
            CapsuleButton(
                text = "Toate ($count)",
                data = "Toate",
                isDark = isDark,
                isSelected = selectedTag.isEmpty(),
                action = {
                    onTagsChanged("")
                }
            )
        }

        tags.forEach { tag ->
            item {
                val itemCount = tagCounts[tag]
                CapsuleButton(
                    text = "$tag ($itemCount)",
                    data = tag,
                    isDark = isDark,
                    isSelected = selectedTag == tag,
                    action = { tag -> onTagsChanged(tag) }
                )
            }
        }
    }
}

@Composable
fun CapsuleButton(text: String, data: String, isDark: Boolean, isSelected: Boolean, action: (String) -> Unit) {

    val bubbleColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("HeaderText", isDark)
    val unselectedText = getNamedColor("MutedText", isDark)

    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = MaterialTheme.typography.caption.fontSize,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) textColor else unselectedText,
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                bubbleColor.copy(alpha = if (isSelected) 1.0f else 0.3f),
                shape = MaterialTheme.shapes.medium,
            )
            .padding(12.dp)
            .clickable { action(data) }
            .defaultMinSize(minWidth = 60.dp)
    )
}