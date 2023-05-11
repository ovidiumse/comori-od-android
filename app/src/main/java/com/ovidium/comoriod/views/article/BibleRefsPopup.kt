package com.ovidium.comoriod.views.article

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.ui.theme.NotoSans
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.formatBibleRefs

@Composable
fun BibleRefsPopup(bibleRefs: List<BibleRefVerse>) {
    Popup(
        alignment = Alignment.BottomCenter,
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val isDark = isSystemInDarkTheme()
        Box(
            Modifier
                .heightIn(max = screenHeight / 3)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    getNamedColor("PrimarySurface", !isDark),
                    RoundedCornerShape(16.dp)
                )
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                itemsIndexed(bibleRefs) { idx, item ->
                    if (idx > 0)
                        Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formatBibleRefs(item, isDark = isDark),
                        style = TextStyle(
                            color = if (isDark) Color.Black else Color.White,
                            fontFamily = NotoSans,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
            }
        }
    }
}