package com.ovidium.comoriod.views.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.colors


@Composable
fun SearchBar(
    searchText: String,
    placeholderText: String = if (searchText.isEmpty()) "Caută..." else searchText,
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onDoneClick: () -> Unit = {}
) {
    var showClearButton: MutableState<Boolean> = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .padding(16.dp)
            .onFocusChanged { focusState ->
                showClearButton.value = (focusState.isFocused)
            }
            .focusRequester(focusRequester),
        value = searchText,
        onValueChange = onSearchTextChanged,
        placeholder = {
            Text(text = placeholderText)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = Color.DarkGray,
            cursorColor = colors.colorPrimary,
            focusedBorderColor = Color.Transparent,
        ),
        trailingIcon = {
            AnimatedVisibility(
                visible = showClearButton.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = { onClearClick() }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close"
                    )
                }

            }
        },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onDoneClick()
            keyboardController?.hide()
        }),
        shape = Shapes.medium
    )



    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}