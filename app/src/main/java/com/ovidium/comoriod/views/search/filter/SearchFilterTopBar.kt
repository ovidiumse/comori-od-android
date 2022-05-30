package com.ovidium.comoriod.views.search.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun FilterViewTopBar(onSaveAction: () -> Unit, onExitAction: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 20.dp)
    ) {
        Text(
            text = "Close",
            color = getNamedColor("Link", isDark = isDark),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .clickable { onExitAction() }
                .fillMaxWidth()
                .weight(0.7f)
                .padding(top = 16.dp, start = 16.dp)
        )
        Text(
            text = "Filtreaza rezultatele",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        )
        Text(
            text = "Save",
            color = getNamedColor("Link", isDark = isDark),
            textAlign = TextAlign.End,
            modifier = Modifier
                .clickable { onSaveAction() }
                .fillMaxWidth()
                .weight(0.7f)
                .padding(top = 16.dp, end = 16.dp)
        )
    }
}