package com.ovidium.comoriod.views.markups

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.parseVerses

@Composable
fun SaveMarkupDialog(
    articleToSave: ArticleResponse,
    selection: String,
    startPos: Int,
    endPos: Int,
    onSaveAction: (Markup) -> Unit,
    onExitAction: () -> Unit
) {

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onExitAction
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        val isDark = isSystemInDarkTheme()
        var tags = remember { mutableStateListOf<String>() }
        var currentTag by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf("markupMorn") }
        var availableColors = listOf("markupChoc", "markupCrayola", "markupMorn", "markupPers", "markupSkye", "markupSlate")


        Box(
            Modifier
                .size(screenWidth, screenHeight)
                .background(
                    getNamedColor("Container", isDark = isDark)!!,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SaveMarkupTopBar(
                    onExitAction = onExitAction
                )
                Text(
                    text = articleToSave.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h5,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Row(horizontalArrangement = Arrangement.Start) {
                    Text(text = "Tag-uri:", modifier = Modifier.padding(end = 5.dp))
                    repeat(tags.count()) { index ->
                        Text(
                            text = tags[index],
                            style = MaterialTheme.typography.caption,
                            color = Color.White,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(Color.Red, RoundedCornerShape(50))
                                .padding(5.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentTag,
                        onValueChange = { text -> currentTag = text },
                        textStyle = TextStyle(fontSize = 12.5.sp),
                        maxLines = 1,
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = getNamedColor("InvertedText", isDark = isDark)!!,
                            cursorColor = getNamedColor("Link", isDark = isDark)!!,
                            focusedBorderColor = Color.Transparent,
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (currentTag.isNotEmpty())
                                    tags.add(currentTag.lowercase())
                                currentTag = ""
                            }
                        ),
                        shape = Shapes.medium,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                    Button(
                        onClick = {
                            if (currentTag.isNotEmpty())
                                tags.add(currentTag.lowercase())
                            currentTag = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = getNamedColor("Link", isDark)!!,
                            contentColor = Color.White),
                        enabled = currentTag.isNotEmpty()
                    ) {
                        Text(text = "+")
                    }
                }
                Card(
                    backgroundColor = getNamedColor(selectedColor, isDark)!!,
                    modifier = Modifier
                        .wrapContentHeight()
                ) {
                    Column() {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "“",
                            color = Color.Black,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = selection,
                            color = Color.Black
                        )
                        Text(
                            text = "„",
                            color = Color.Black,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .background(Color.DarkGray)
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            repeat(availableColors.count()) { index ->
                                Box(
                                    modifier = Modifier
                                        .border(BorderStroke( 2.dp,if (selectedColor == availableColors[index]) Color.White else Color.Transparent), CircleShape)
                                        .background(
                                            getNamedColor(availableColors[index], isDark)!!,
                                            shape = CircleShape
                                        )
                                        .padding(8.dp)
                                        .size(20.dp)
                                        .clickable {
                                            selectedColor = availableColors[index]
                                        }
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        val tempMarkup = Markup(
                            id = "",
                            title = articleToSave.title,
                            book = articleToSave.book,
                            articleID = articleToSave._id,
                            selection = selection,
                            index = startPos,
                            length = endPos - startPos,
                            bgColor = selectedColor
                        )
                        if (tags.isNotEmpty()) {
                            tempMarkup.tags = tags
                            onSaveAction(tempMarkup)
                        } else {
                            tempMarkup.tags = listOf(currentTag)
                            onSaveAction(tempMarkup)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = getNamedColor("Link", isDark)!!,
                        contentColor = Color.White),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 200.dp, minHeight = 50.dp)
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Salvează")
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp, top = 10.dp)
    ) {
        Text(
            text = "Salvează pasajul",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = getNamedColor("Link", isDark = isDark)!!,
            modifier = Modifier
                .clickable(onClick = onExitAction)
        )
    }
}