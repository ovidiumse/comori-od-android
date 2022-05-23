@file:Suppress("UNUSED", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.ovidium.comoriod.components.selection

import androidx.compose.foundation.text.detectDragGesturesWithObserver
import androidx.compose.foundation.text.isInTouchMode
import androidx.compose.foundation.text.selection.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalTextToolbar

@Composable
fun SelectionContainer(
    modifier: Modifier = Modifier,
    clearSelection: Boolean,
    onSelectionChange: (Int, Int) -> Unit,
    content: @Composable () -> Unit
) {
    var selection by remember { mutableStateOf<Selection?>(null) }
    if (clearSelection)
        selection = null

    SelectionContainer(
        modifier = modifier,
        selection = selection,
        onSelectionChange = { sel ->
            selection = sel

            if (selection?.start?.offset == null)
                return@SelectionContainer

            if (selection!!.start.offset > selection!!.end.offset)
                onSelectionChange(selection!!.end.offset, selection!!.start.offset)
            else
                onSelectionChange(selection!!.start.offset, selection!!.end.offset)
        },
        children = content
    )
}

@Composable
internal fun SelectionContainer(
    /** A [Modifier] for SelectionContainer. */
    modifier: Modifier = Modifier,
    /** Current Selection status.*/
    selection: Selection?,
    /** A function containing customized behaviour when selection changes. */
    onSelectionChange: (Selection?) -> Unit,
    children: @Composable () -> Unit
) {
    val registrarImpl = remember { SelectionRegistrarImpl() }
    val manager = remember { SelectionManager(registrarImpl) }

    manager.hapticFeedBack = LocalHapticFeedback.current
    manager.clipboardManager = LocalClipboardManager.current
    manager.textToolbar = LocalTextToolbar.current
    manager.onSelectionChange = onSelectionChange
    manager.selection = selection
    manager.touchMode = isInTouchMode

    if (selection?.start?.offset == null)
        manager.onRelease()

    CompositionLocalProvider(LocalSelectionRegistrar provides registrarImpl) {
        // Get the layout coordinates of the selection container. This is for hit test of
        // cross-composable selection.
        SimpleLayout(modifier = modifier.then(manager.modifier)) {
            children()
            if (isInTouchMode && manager.hasFocus) {
                manager.selection?.let {

                    listOf(true, false).forEach { isStartHandle ->
                        val observer = remember(isStartHandle) {
                            manager.handleDragObserver(isStartHandle)
                        }
                        val position = if (isStartHandle) {
                            manager.startHandlePosition
                        } else {
                            manager.endHandlePosition
                        }

                        val direction = if (isStartHandle) {
                            it.start.direction
                        } else {
                            it.end.direction
                        }

                        if (position != null) {
                            SelectionHandle(
                                position = position,
                                isStartHandle = isStartHandle,
                                direction = direction,
                                handlesCrossed = it.handlesCrossed,
                                modifier = Modifier.pointerInput(observer) {
                                    detectDragGesturesWithObserver(observer)
                                },
                                content = null
                            )
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(manager) {
        onDispose {
            manager.hideSelectionToolbar()
        }
    }
}
