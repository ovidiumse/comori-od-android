package com.ovidium.comoriod.views.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.AdaptiveText
import com.ovidium.comoriod.data.autocomplete.Hit
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.Screens
import java.net.URLEncoder


@Composable
fun AutocompleteCell(hit: Hit, navController: NavController, isDark: Boolean) {
    val textColor = getNamedColor("Text", isDark)
    val mutedTextColor = getNamedColor("MutedText", isDark)

    Column(modifier = Modifier
        .clickable {
            navController.navigate(
                Screens.Article.withArgs(URLEncoder.encode(hit._id, "utf-8"))
            ) {
                launchSingleTop = true
            }
        }) {
        AdaptiveText(
            text = hit._source.title,
            style = MaterialTheme.typography.body1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(14.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
                    contentDescription = "Book icon",
                    tint = mutedTextColor
                )
                Spacer(modifier = Modifier.width(5.dp))
                AdaptiveText(
                    text = hit._source.book,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = mutedTextColor
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(14.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_author),
                    contentDescription = "Author icon",
                    tint = mutedTextColor
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = hit._source.author,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = mutedTextColor
                )
            }
        }

        Divider(
            color = mutedTextColor.copy(alpha = 0.5f),
            thickness = 0.3.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}