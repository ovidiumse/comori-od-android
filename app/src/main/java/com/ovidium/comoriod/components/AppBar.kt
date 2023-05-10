package com.ovidium.comoriod.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun AppBar(
    title: String? = null,
    onMenuClicked: () -> Unit,
    onTitleClicked: () -> Unit,
    actions: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val background = getNamedColor("HeaderBar", isDark = isDark)
    val headerText = getNamedColor("HeaderText", isDark = isDark)

    TopAppBar(
        title = {
            Spacer(modifier = Modifier.width(8.dp))

            AdaptiveText(
                text = title ?: stringResource(id = R.string.app_title),
                minFontSize = 8.sp,
                maxFontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = headerText,
                modifier = Modifier.clickable(onClick = onTitleClicked)
            )
        },
        modifier = Modifier.height(54.dp),
        navigationIcon = {
            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.clickable(onClick = onMenuClicked),
                tint = headerText
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(modifier = Modifier.size(48.dp), onClick = onTitleClicked) {
                Image(painter = painterResource(id = R.drawable.comoriod_logo_512), "Logo", modifier = Modifier.size(38.dp))
            }
        },
        elevation = 8.dp,
        backgroundColor = background,
        actions = {
            actions()
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}