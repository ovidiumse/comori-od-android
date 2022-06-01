package com.ovidium.comoriod.components


import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.ovidium.comoriod.R
import com.ovidium.comoriod.google.GoogleApiContract
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.Screens
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    applicationContext: Context,
    drawerState: DrawerState,
    navController: NavController
) {
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

    val coroutineScope = rememberCoroutineScope()

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
                val painter = when (userResource.state) {
                    UserState.LoggedIn -> rememberAsyncImagePainter(userResource.user?.photoUrl)
                    else -> painterResource(id = R.drawable.ic_unknown_person)
                }

                Image(
                    painter,
                    "Profile picture",
                    modifier = Modifier
                        .width(80.dp)
                        .height(if (userResource.state == UserState.LoggedIn) 80.dp else 82.dp)
                        .clip(RoundedCornerShape(100))
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (userResource.state == UserState.LoggedIn)
                        Column {
                            userResource.user?.displayName?.let { Text(it) }
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
                                    else -> {}
                                }
                            }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (userResource.state == UserState.LoggedIn) {
                item() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(top = 12.dp)
                            .clickable {
                                navController.navigate(Screens.Favorites.route)
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                            }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_star_24),
                            contentDescription = "Favorites",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Favorite",
                            style = MaterialTheme.typography.h6,
                        )
                    }
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable {
                                navController.navigate(Screens.Markups.route)
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                            }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_article_24),
                            contentDescription = "Markups",
                            modifier = Modifier
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "Pasaje",
                            style = MaterialTheme.typography.h6,
                        )
                    }

                }
            } else {

            }
        }
    }
}
