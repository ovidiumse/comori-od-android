package com.ovidium.comoriod.views.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.R
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun NoSearchResultsPlaceholder(query: String, isAutocomplete: Boolean, searchInTextAction: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_search_24),
            contentDescription = "Search",
            tint = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
            modifier = Modifier
                .size(40.dp)
        )
        Text(
            text = if (isAutocomplete) "Nu sunt rezultate în titluri" else "Nu sunt rezultate pentru '${query}'",
            style = MaterialTheme.typography.h6,
            color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
        )
        if (isAutocomplete) {
            Text(
                text = "Caută și în text",
                color = getNamedColor("Link", isSystemInDarkTheme()),
                modifier = Modifier
                    .clickable { searchInTextAction() }
            )
        }
    }
}