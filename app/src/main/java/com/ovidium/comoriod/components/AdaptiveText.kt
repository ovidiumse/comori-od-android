package com.ovidium.comoriod.components

import android.util.Log
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

fun areWordsTruncated(textLayoutResult: TextLayoutResult): Boolean {
    var truncatesWords = false
    for (idx in 0 until textLayoutResult.lineCount) {
        val lineEnd = textLayoutResult.getLineEnd(idx, true)
        val range = textLayoutResult.getWordBoundary(lineEnd)
        if (range.end != lineEnd) {
            truncatesWords = true
            break
        }
    }

    return truncatesWords
}

@Composable
fun AdaptiveText(
    text: String,
    minFontSize: TextUnit,
    maxFontSize: TextUnit = 48.sp,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    var textStyle by remember { mutableStateOf(style) }
    var textStyleReady by remember { mutableStateOf(false) }

    val fontSizeUpdateRatio = 0.9
    val fontScale = LocalDensity.current.fontScale

    Log.d("AdaptiveText", "$text is now ${textStyle.fontSize} at $fontScale scale")

    fun isDriftAllowed(): Boolean {
        return (textStyle.fontSize * fontSizeUpdateRatio * fontScale) > minFontSize
    }

    fun isFontTooBig(): Boolean {
        return (textStyle.fontSize * fontScale) > maxFontSize
    }

    Text(
        text,
        modifier.drawWithContent { if (textStyleReady) drawContent() },
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        onTextLayout = { textLayoutResult ->
            if (isDriftAllowed() && (areWordsTruncated(textLayoutResult) || textLayoutResult.hasVisualOverflow || isFontTooBig()))
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * fontSizeUpdateRatio)
            else
                textStyleReady = true

            onTextLayout(textLayoutResult)
        },
        textStyle
    )
}

@Composable
fun AdaptiveText(
    text: AnnotatedString,
    minFontSize: TextUnit,
    maxFontSize: TextUnit = 48.sp,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    var textStyle by remember { mutableStateOf(style) }
    var textStyleReady by remember { mutableStateOf(false) }

    val fontSizeUpdateRatio = 0.9
    val fontScale = LocalDensity.current.fontScale

    Log.d("AdaptiveText", "$text is now ${textStyle.fontSize} at $fontScale scale")

    fun isDriftAllowed(): Boolean {
        return (textStyle.fontSize * fontSizeUpdateRatio * fontScale) > minFontSize
    }

    fun isFontTooBig(): Boolean {
        return (textStyle.fontSize * fontScale) > maxFontSize
    }

    Text(
        text,
        modifier.drawWithContent { if (textStyleReady) drawContent() },
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        inlineContent,
        onTextLayout = { textLayoutResult ->
            if (isDriftAllowed() && (areWordsTruncated(textLayoutResult) || textLayoutResult.hasVisualOverflow || isFontTooBig()))
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * fontSizeUpdateRatio)
            else
                textStyleReady = true

            onTextLayout(textLayoutResult)
        },
        textStyle
    )
}