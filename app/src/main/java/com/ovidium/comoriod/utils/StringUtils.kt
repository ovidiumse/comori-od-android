package com.ovidium.comoriod.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.ovidium.comoriod.data.bible.AnnotationString

fun String.toSpanStyle(
    style: SpanStyle,
    annotationString: AnnotationString? = null
): AnnotatedString {
    return buildAnnotatedString {
        append(this@toSpanStyle)
        addStyle(style, 0, this@toSpanStyle.length)

        annotationString?.let {
            addStringAnnotation(
                tag = it.tag,
                annotation = it.annotation,
                start = 0,
                end = this@toSpanStyle.length
            )
        }
    }
}

fun String.addPrefix(prefix: String) = "$prefix$this"
fun String.addSuffix(suffix: String) = "$this$suffix"