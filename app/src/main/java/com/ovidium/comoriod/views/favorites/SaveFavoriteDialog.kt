package com.ovidium.comoriod.views.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun SaveFavoriteDialog(
    articleToSave: Article,
    onSaveAction: (List<String>) -> Unit,
    onExitAction: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val isDark = isSystemInDarkTheme()
    val bgColor = getNamedColor("Background", isDark)
    val textColor = getNamedColor("Text", isDark)
    val primarySurfaceColor = getNamedColor("PrimarySurface", isDark)

    var tags = remember { mutableStateListOf<String>() }
    var currentTag by remember { mutableStateOf("") }
    var resetTag by remember { mutableStateOf(false) }

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
            Column(
                modifier = Modifier
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SaveFavoriteTopBar(
                    onExitAction = onExitAction
                )

                FavoriteArticleCell(
                    title = articleToSave.title.text,
                    author = articleToSave.author,
                    book = articleToSave.book,
                    timestamp = null,
                    tags = tags,
                    isDark = isDark
                ) {

                }

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

                Button(
                    onClick = {
                        if (tags.isNotEmpty()) {
                            onSaveAction(tags)
                        } else {
                            onSaveAction(emptyList())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = getNamedColor("Link", isDark),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.defaultMinSize(minWidth = 120.dp)
                ) {
                    Text(text = "Salvează")
                }
            }
        }
    }
}


@Composable
fun SaveFavoriteTopBar(
    onExitAction: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val textColor = getNamedColor("Text", isDark)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = "Salvare articol favorit",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = textColor,
            modifier = Modifier.clickable(onClick = onExitAction)
        )
    }
}