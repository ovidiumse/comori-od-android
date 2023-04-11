package com.ovidium.comoriod.views.search

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalLifecycleOwner
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
    searchText: TextFieldValue,
    shouldFocus: Boolean,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    placeholderText: String = searchText.text.ifEmpty { "Caută..." },
    onSearchTextChanged: (TextFieldValue) -> Unit = {},
    onClearClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val showClearButton: MutableState<Boolean> = remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) };

    OutlinedTextField(
        modifier = Modifier
            .height(52.dp)
            .onFocusChanged { focusState ->
                showClearButton.value = focusState.hasFocus
                isFocused = focusState.hasFocus
            }
            .focusRequester(focusRequester)
            .then(modifier),
        value = searchText,
        onValueChange = { newFieldValue ->
            onSearchTextChanged(newFieldValue)
        },
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
                IconButton(modifier = Modifier.size(48.dp), onClick = { onClearClick() }) {
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
        shape = RoundedCornerShape(12.dp)
    )

    LaunchedEffect(Unit) {
        if (isFocused || shouldFocus)
            focusRequester.requestFocus()
    }

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            isFocused = false
        }
    }
}