package com.ovidium.comoriod.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.R

@Composable
fun GoogleButton(
    text: String = "Loghează-te cu Google",
    loadingText: String = "Se loghează...",
    loading: Boolean = false,
    borderColor: Color = Color.LightGray,
    progressIndicatorColor: Color = MaterialTheme.colors.primaryVariant,
    onClicked: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = MaterialTheme.colors.surface,
        modifier = Modifier.clickable { onClicked() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_icon),
                contentDescription = "Google Button",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (loading) loadingText else text)
            if (loading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )
            }
        }
    }
}

@Composable
@Preview
fun GoogleButtonPreview() {
    GoogleButton(onClicked = {})
}