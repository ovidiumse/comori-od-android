package com.ovidium.comoriod.views.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.colors
import com.ovidium.comoriod.ui.theme.getNamedColor


@Composable
fun SearchBar(
    searchText: String,
    focusRequester: FocusRequester,
    placeholderText: String = if (searchText.isEmpty()) "Caută..." else searchText,
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    var showClearButton: MutableState<Boolean> = remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(vertical = 5.dp, horizontal = 5.dp)
            .onFocusChanged { focusState ->
                showClearButton.value = (focusState.isFocused)
            }
            .focusRequester(focusRequester),
        value = searchText,
        onValueChange = onSearchTextChanged,
        placeholder = {
            Text(text = placeholderText, fontSize = 12.5.sp)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = getNamedColor("InvertedText", isDark = isSystemInDarkTheme())!!,
            cursorColor = getNamedColor("Link", isDark = isSystemInDarkTheme())!!,
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
        textStyle = TextStyle(fontSize = 12.5.sp),
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
        shape = Shapes.medium
    )



    LaunchedEffect(Unit) {
        if (searchText.isEmpty()) {
            focusRequester.requestFocus()
        }
    }
}