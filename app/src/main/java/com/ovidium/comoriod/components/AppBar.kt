package com.ovidium.comoriod.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.R

@Composable
fun AppBar(onMenuClicked: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val background = if (isDark) MaterialTheme.colors.background else MaterialTheme.colors.primary

    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_title),
                color = Color.White
            )
        },
        navigationIcon = {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.clickable(onClick = onMenuClicked),
                tint = Color.White
            )
        },
        elevation = 8.dp,
        backgroundColor = background
    )
}