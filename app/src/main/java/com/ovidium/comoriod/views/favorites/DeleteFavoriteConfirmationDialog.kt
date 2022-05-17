package com.ovidium.comoriod.views.favorites

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun DeleteFavoriteConfirmationDialog(
    deleteAction: () -> Unit,
    dismissAction: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = {
            Text(text = "Atenție")
        },
        text = {
            Text("Ești sigur că vrei să ștergi acest articol favorit?")
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White
                ),
                onClick = {
                    deleteAction()
                }) {
                Text("Șterge")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = getNamedColor("Link", isSystemInDarkTheme())!!,
                    contentColor = Color.White
                ),
                onClick = {
                    dismissAction()
                }) {
                Text("Renunță")
            }
        }
    )
}