package com.ovidium.comoriod.components


import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ovidium.comoriod.R
import com.ovidium.comoriod.google.GoogleApiContract
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.Screens
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    applicationContext: Context,
    drawerState: DrawerState,
    navController: NavController
) {
    val isDark = isSystemInDarkTheme()
    val lineColor = if (isDark) Color.LightGray else Color.DarkGray

    val signInRequestCode = 1

    val signInModel: GoogleSignInModel = viewModel()

    val authLauncher =
        rememberLauncherForActivityResult(contract = GoogleApiContract()) { task ->
            signInModel.retrieveUser(task)
        }

    val userResourceState = signInModel.userResource
    val userResource = userResourceState.value

    val surfaceColor = getNamedColor("PrimarySurface", isDark)
    val bubbleColor = getNamedColor("Bubble", isDark)
    val headerColor = getNamedColor("HeaderBar", isDark)
    val textColor = getNamedColor("HeaderText", isDark)

    if (userResource.state == UserState.Unknown)
        signInModel.silentSignIn(applicationContext)

    val coroutineScope = rememberCoroutineScope()

    var signInPending by remember { mutableStateOf(false) }
    var signOutPending by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(headerColor)
                .drawBehind {
                    val strokeWidth = 0.5f
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

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (userResource.state == UserState.LoggedIn)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            userResource.user?.displayName?.let { name ->
                                AdaptiveText(
                                    text = name,
                                    color = textColor,
                                    style = MaterialTheme.typography.h6,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    signInModel.signOut(applicationContext)
                                    signOutPending = true
                                },
                                colors = ButtonDefaults.outlinedButtonColors(bubbleColor, textColor),
                                modifier = Modifier
                                    .padding(top = 3.dp)
                            ) {
                                AdaptiveText(
                                    text = "Deloghează-te",
                                    color = textColor,
                                    style = MaterialTheme.typography.caption,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
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
                            borderColor = textColor.copy(alpha = 0.3f),
                            bgColor = bubbleColor,
                            loading = userResource.state == UserState.Loading,
                            onClicked = {
                                when (userResource.state) {
                                    UserState.Error, UserState.NotLoggedIn -> {
                                        signInModel.signIn(authLauncher, signInRequestCode)
                                        signInPending = true
                                    }

                                    else -> {}
                                }
                            }
                        )
                    }
                }
            }
        }

        LaunchedEffect(userResource.state) {
            if (signInPending && userResource.state == UserState.LoggedIn) {
                coroutineScope.launch {
                    drawerState.close()
                }
            } else if (signOutPending && userResource.state == UserState.NotLoggedIn) {
                coroutineScope.launch {
                    drawerState.close()
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceColor)
        ) {
            if (userResource.state == UserState.LoggedIn) {
                item {
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
                            modifier = Modifier.padding(end = 8.dp),
                            tint = textColor
                        )
                        Text(
                            text = "Favorite",
                            style = MaterialTheme.typography.h6,
                            color = textColor
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
                                .padding(end = 8.dp),
                            tint = textColor
                        )
                        Text(
                            text = "Pasaje",
                            style = MaterialTheme.typography.h6,
                            color = textColor
                        )
                    }

                }
            } else {

            }
        }
    }
}
