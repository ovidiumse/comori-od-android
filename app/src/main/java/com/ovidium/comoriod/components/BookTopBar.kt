package com.ovidium.comoriod.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.R
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun BookTopBar(title: @Composable () -> Unit, onMenuClicked: () -> Unit, onTOCClicked: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val background = getNamedColor("Container", isDark = isDark)!!

    TopAppBar(
        title = {
            title()
        },
        navigationIcon = {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.clickable(onClick = onMenuClicked),
                tint = getNamedColor("Link", isDark = isDark)!!
            )
        },
        elevation = 8.dp,
        backgroundColor = background,
        actions = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Cuprins",
                    modifier = Modifier.clickable(onClick = onTOCClicked),
                    tint = getNamedColor("Link", isDark = isDark)!!
                )
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}