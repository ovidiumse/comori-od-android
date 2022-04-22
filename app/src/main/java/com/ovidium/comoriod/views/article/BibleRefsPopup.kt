package com.ovidium.comoriod.views.article

import android.graphics.fonts.Font
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.ui.theme.colors
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.formatBibleRefs

@Composable
fun BibleRefsPopup(bibleRefs: List<BibleRefVerse>) {
    Popup(
        alignment = Alignment.BottomCenter,
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        val isDark = isSystemInDarkTheme()
        Box(
            Modifier
                .size(screenWidth, screenHeight / 3)
                .background(
                    getNamedColor("PopupContainer", isDark = isDark)!!,
                    RoundedCornerShape(16.dp)
                )
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                itemsIndexed(bibleRefs) { index, item ->
                    Text(
                        text = formatBibleRefs(item, isDark = isDark),
                        style = TextStyle(
                            color = getNamedColor("InvertedText", isDark = isDark)!!,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Light
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }
    }
}