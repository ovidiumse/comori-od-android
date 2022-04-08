package com.ovidium.comoriod.views


import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.ovidium.comoriod.R
import com.ovidium.comoriod.components.GoogleButton
import com.ovidium.comoriod.google.GoogleApiContract
import com.ovidium.comoriod.google.GoogleUserModel
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.GoogleSignInModelFactory
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import kotlinx.coroutines.coroutineScope
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun Drawer(applicationContext: Context?) {
    val isDark = isSystemInDarkTheme()
    val lineColor = if (isDark) Color.DarkGray else Color.LightGray

    val signInRequestCode = 1

    val signInModel: GoogleSignInModel = viewModel()

    val authLauncher =
        rememberLauncherForActivityResult(contract = GoogleApiContract()) { task ->
            signInModel.retrieveUser(task)
        }

    val userResourceState = signInModel.userResource
    val userResource = userResourceState.value

    if (userResource.state == UserState.Unknown)
        signInModel.silentSignIn(applicationContext)

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .drawBehind {
                    val strokeWidth = 2f
                    val y = size.height - strokeWidth / 2

                    drawLine(
                        lineColor,
                        Offset(0f, y),
                        Offset(size.width, y),
                        strokeWidth
                    )
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                val painter = when (userResource?.state) {
                    UserState.LoggedIn -> rememberImagePainter(userResource.user?.photoUrl)
                    else -> painterResource(id = R.drawable.ic_unknown_person)
                }

                Image(
                    painter,
                    "Profile picture",
                    modifier = Modifier
                        .width(80.dp)
                        .height(if (userResource?.state == UserState.LoggedIn) 80.dp else 82.dp)
                        .clip(RoundedCornerShape(100))
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (userResource.state == UserState.LoggedIn)
                        Column {
                            Text(userResource.user!!.displayName)
                            Button(onClick = { signInModel.signOut(applicationContext) }) {
                                Text("Deloghează-te")
                            }
                        }
                    else {
                        if (userResource.state == UserState.Error)
                            Toast.makeText(
                                LocalContext.current,
                                userResource.message!!,
                                Toast.LENGTH_SHORT
                            ).show()

                        GoogleButton(
                            text = "Loghează-te",
                            borderColor = lineColor,
                            loading = userResource.state == UserState.Loading,
                            onClicked = {
                                when (userResource.state) {
                                    UserState.Error, UserState.NotLoggedIn ->
                                        signInModel.signIn(authLauncher, signInRequestCode)
                                }
                            }
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            repeat(5) { item ->
                Row() {
                    Text(
                        text = "Item number $item",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

@ExperimentalTime
@Composable
@Preview
fun DrawerPreview() {
    Drawer(null)
}