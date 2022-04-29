package com.ovidium.comoriod.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.R
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.search.SearchBar
import kotlinx.coroutines.launch

@Composable
fun AppBar(onMenuClicked: () -> Unit, onSearchClicked: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val background = getNamedColor("Container", isDark = isDark)!!

    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_title),
                color = getNamedColor("Link", isDark = isDark)!!
            )
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
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.clickable(onClick = onSearchClicked),
                tint = getNamedColor("Link", isDark = isDark)!!
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}