@file:Suppress("UNUSED", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.ovidium.comoriod.components.selection

import androidx.compose.foundation.text.selection.Selection
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun SelectionContainer(
    modifier: Modifier = Modifier,
    onSelectionChange: (Int?, Int?) -> Unit,
    content: @Composable () -> Unit
) {
    var selection by remember { mutableStateOf<Selection?>(null) }
    androidx.compose.foundation.text.selection.SelectionContainer(
        modifier = modifier,
        selection = selection,
        onSelectionChange = {
            selection = it
            onSelectionChange(selection?.start?.offset, selection?.end?.offset)
        },
        children = content
    )
}