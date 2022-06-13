package com.ovidium.comoriod.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TagBubble(tag: String, textColor: Color, bubbleColor: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .background(bubbleColor)
    ) {
        Text(
            text = tag,
            color = textColor,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(vertical = 3.dp)
                .padding(horizontal = 5.dp)
        )
    }
}