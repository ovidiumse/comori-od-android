package com.ovidium.comoriod.views.search

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
import com.ovidium.comoriod.R
import com.ovidium.comoriod.data.autocomplete.Hit


@Composable
fun AutocompleteCell(hit: Hit) {
    Column {
        Text(
            text = hit._source.title,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(vertical = 3.dp)
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