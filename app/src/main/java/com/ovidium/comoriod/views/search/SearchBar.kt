package com.ovidium.comoriod.views.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.colors
import com.ovidium.comoriod.ui.theme.getNamedColor


@Composable
fun SearchBar(
    searchText: String,
    focusRequester: FocusRequester,
    placeholderText: String = searchText.ifEmpty { "Caută..." },
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val showClearButton: MutableState<Boolean> = remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp, horizontal = 3.dp)
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
            backgroundColor = getNamedColor("Background", isDark),
            textColor = getNamedColor("MutedText", isDark),
            cursorColor = getNamedColor("MutedText", isDark),
            focusedBorderColor = getNamedColor("Border", isDark),
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
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
        shape = RoundedCornerShape(15.dp)
    )

    LaunchedEffect(Unit) {
        if (searchText.isEmpty()) {
            focusRequester.requestFocus()
        }
    }
}