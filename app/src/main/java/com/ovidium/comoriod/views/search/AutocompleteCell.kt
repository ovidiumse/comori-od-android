package com.ovidium.comoriod.views.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.autocomplete.Hit
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.Screens
import java.net.URLEncoder


@Composable
fun AutocompleteCell(hit: Hit, navController: NavController, isDark: Boolean) {
    val textColor = getNamedColor("Text", isDark)

    Column {
        Text(
            text = hit._source.title,
            color = textColor,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(vertical = 3.dp)
                .clickable {
                    navController.navigate(
                        Screens.Article.withArgs(
                            URLEncoder.encode(
                                hit._id,
                                "utf-8"
                            )
                        )
                    ) {
                        launchSingleTop = true
                    }
                }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_book),
                contentDescription = "Some icon",
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = hit._source.book,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(vertical = 3.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}