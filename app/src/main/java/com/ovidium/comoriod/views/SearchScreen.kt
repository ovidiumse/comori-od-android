package com.ovidium.comoriod.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column {
            Surface(modifier = Modifier.fillMaxWidth(), elevation = 8.dp) {

            }

            TextField(
                value = query,
                onValueChange = { newText -> query = newText },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background),
//                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background),
                label = { Text("Caută în Comori OD") })
            if (query.isNotEmpty()) {
                Text("You entered $query")
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}