package com.ovidium.comoriod.views.article

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Colors
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.components.ActionCallback
import com.ovidium.comoriod.components.CustomTextToolbar
import com.ovidium.comoriod.components.selection.SelectionContainer
import com.ovidium.comoriod.data.article.Article
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.NotoSans
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun ArticleBodyView(
    article: Article,
    markupSelection: MutableState<String>,
    markups: List<Markup>,
    highlights: SnapshotStateList<TextRange>,
    offsetList: SnapshotStateList<Int>,
    currentHighlightIndex: MutableState<Int?>,
    receivedMarkupIndex: MutableState<Int>,
    receivedMarkupLength: MutableState<Int>,
    markupId: String?,
    textColor: Color,
    handleColor: Color,
    startPos: MutableState<Int>,
    endPos: MutableState<Int>,
    scrollOffset: MutableState<Int>,
    bibleRefs: SnapshotStateList<BibleRefVerse>,
    showHighlightControls: MutableState<Boolean>,
    signInModel: GoogleSignInModel,
    onTextClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var selection by remember { mutableStateOf("") }
    var clearSelection by remember { mutableStateOf(false) }
    var scrollTopOffset = 0
    val isDark = isSystemInDarkTheme()

    var onHighlight: ActionCallback? = null
    if (signInModel.userResource.value.state == UserState.LoggedIn)
        onHighlight = {
            markupSelection.value = selection
            clearSelection = true
        }

    with(LocalDensity.current) {
        scrollTopOffset =
            (LocalConfiguration.current.screenHeightDp / 3).dp.toPx().toInt()
    }

    val textToolbar = CustomTextToolbar(
        LocalView.current,
        onCopy = {
            clearSelection = true
        },
        onHighlight = onHighlight
    )

    val customTextSelectionColors = TextSelectionColors(
        handleColor = handleColor,
        backgroundColor = handleColor.copy(alpha = 0.3f)
    )

    fun renderWithOptions(): AnnotatedString {
        return if (receivedMarkupLength.value != 0) {
            val markupSelectionStyle = SpanStyle(
                background = handleColor.copy(alpha = 0.3f)
            )

            buildAnnotatedString {
                append(article.body)
                addStyle(markupSelectionStyle, receivedMarkupIndex.value, receivedMarkupIndex.value + receivedMarkupLength.value)
            }
        } else {
            article.body
        }
    }

    CompositionLocalProvider(
        LocalTextToolbar provides textToolbar,
        LocalTextSelectionColors provides customTextSelectionColors
    ) {
        SelectionContainer(
            clearSelection = clearSelection,
            onSelectionChange = { start, end ->
                selection = article.body.text.subSequence(start, end).toString()
                startPos.value = start
                endPos.value = end
            },
            modifier = modifier
        ) {
            ClickableText(
                text = renderWithOptions(),
                style = TextStyle(
                    fontFamily = NotoSans,
                    color = textColor,
                    fontSize = 18.sp,
                    lineHeight = 25.sp
                ),
                modifier = Modifier.fillMaxSize(),
                onTextLayout = { textLayout ->
                    val markup = markups.firstOrNull { markup -> markup.id == markupId }
                    if (markup != null) {
                        val rectStart = textLayout.getBoundingBox(markup.index)
                        scrollOffset.value = (rectStart.topLeft.y - scrollTopOffset).coerceAtLeast(0f).toInt()
                    }
                    else if (receivedMarkupLength.value != 0) {
                        val rectStart = textLayout.getBoundingBox(receivedMarkupIndex.value)
                        scrollOffset.value = (rectStart.topLeft.y - scrollTopOffset).coerceAtLeast(0f).toInt()
                    }

                    if (offsetList.isEmpty()) {
                        for (highlight in highlights) {
                            val rectStart = textLayout.getBoundingBox(highlight.start)
                            val offset = (rectStart.topLeft.y - scrollTopOffset).coerceAtLeast(0f).toInt()
                            offsetList.add(offset)
                        }
                    }
                }
            ) { offset ->
                val annotation = article.body.getStringAnnotations(
                    tag = "URL",
                    start = offset,
                    end = offset
                ).firstOrNull()

                bibleRefs.clear()
                if (annotation != null)
                    bibleRefs.addAll(article.bibleRefs[annotation.item]!!.verses)

                clearSelection = true
                textToolbar.hide()
                if (bibleRefs.isEmpty()) {
                    onTextClick()
                    showHighlightControls.value = showHighlightControls.value.not()
                    if (showHighlightControls.value) {
                        val hlAnnotations =
                            article.body.spanStyles.filter { it.item.background == Color.Transparent }
                        highlights.addAll(hlAnnotations.map { TextRange(it.start, it.end) })
                    } else {
                        highlights.clear()
                        offsetList.clear()
                    }
                }
            }
        }

        DisposableEffect(textToolbar) {
            onDispose { textToolbar.hide() }
        }
        clearSelection = false
    }
}