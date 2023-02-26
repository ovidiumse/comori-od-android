package com.ovidium.comoriod.views.markups

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun SaveMarkupDialog(
    articleToSave: Article,
    selection: String,
    startPos: Int,
    endPos: Int,
    onSaveAction: (Markup) -> Unit,
    onExitAction: () -> Unit
) {
    val listState = rememberLazyListState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val isDark = isSystemInDarkTheme()
    val bgColor = getNamedColor("Background", isDark)
    val textColor = getNamedColor("Text", isDark)
    val primarySurfaceColor = getNamedColor("PrimarySurface", isDark)

    var tags = remember { mutableStateListOf<String>() }
    var currentTag by remember { mutableStateOf("") }
    var resetTag by remember { mutableStateOf(false) }

    var selectedColor by remember { mutableStateOf("markupMorn") }

    var availableColors = listOf(
        "markupChoc",
        "markupCrayola",
        "markupMorn",
        "markupPers",
        "markupSkye",
        "markupSlate"
    )

    if (resetTag) {
        currentTag = ""
        resetTag = false
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onExitAction
    ) {
        Box(
            Modifier
                .width(screenWidth)
                .background(
                    bgColor,
                    RoundedCornerShape(16.dp)
                )
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SaveMarkupTopBar(onExitAction = onExitAction)
                }

                item {
                    Row() {
                        MarkupCell(
                            textSelection = selection,
                            markupColor = selectedColor,
                            timestamp = null,
                            author = articleToSave.author,
                            title = articleToSave.title.text,
                            book = articleToSave.book,
                            tags = tags,
                            isDark = isDark
                        ) {

                        }
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        fun addTag(tag: String) {
                            if (tag.isNotEmpty())
                                tags.add(tag)
                            resetTag = true
                        }

                        Text(text = "Tag-uri:")

                        OutlinedTextField(
                            value = currentTag,
                            onValueChange = { text -> currentTag = text },
                            maxLines = 1,
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                backgroundColor = getNamedColor("PrimarySurface", isDark),
                                textColor = getNamedColor("MutedText", isDark),
                                cursorColor = getNamedColor("MutedText", isDark),
                                focusedBorderColor = getNamedColor("Border", isDark)
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    addTag(currentTag.trim().lowercase())
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.85f)
                        )

                        Button(
                            onClick = {
                                addTag(currentTag.trim().lowercase())
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = getNamedColor("Link", isDark),
                                disabledBackgroundColor = primarySurfaceColor,
                                contentColor = Color.White
                            ),
                            enabled = currentTag.isNotEmpty(),
                            modifier = Modifier.weight(0.15f)
                        ) {
                            Text(text = "+")
                        }
                    }
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .background(primarySurfaceColor, shape = RoundedCornerShape(16.dp))
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        availableColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .border(
                                        BorderStroke(
                                            2.dp,
                                            if (selectedColor == color) textColor else Color.Transparent
                                        ), CircleShape
                                    )
                                    .background(
                                        getNamedColor(color, isDark),
                                        shape = CircleShape
                                    )
                                    .padding(8.dp)
                                    .size(20.dp)
                                    .clickable {
                                        selectedColor = color
                                    }
                            )
                        }
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                onSaveAction(
                                    Markup(
                                        id = "",
                                        title = articleToSave.title.text,
                                        book = articleToSave.book,
                                        articleID = articleToSave.id,
                                        selection = selection,
                                        index = startPos,
                                        length = endPos - startPos,
                                        author = articleToSave.author,
                                        type = articleToSave.type,
                                        bgColor = selectedColor,
                                        tags = tags
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = getNamedColor("Link", isDark),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 120.dp)
                        ) {
                            Text(text = "Salvează")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SaveMarkupTopBar(
    onExitAction: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val textColor = getNamedColor("Text", isDark)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = "Salvare pasaj",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = textColor,
            modifier = Modifier.clickable(onClick = onExitAction)
        )
    }
}

