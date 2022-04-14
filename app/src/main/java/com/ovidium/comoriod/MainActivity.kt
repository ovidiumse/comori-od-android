package com.ovidium.comoriod

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.components.BottomNavigation
import com.ovidium.comoriod.components.Drawer
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.GoogleSignInModelFactory
import com.ovidium.comoriod.ui.theme.ComoriODTheme
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.*
import com.ovidium.comoriod.views.search.SearchScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComoriOdApp(applicationContext)
        }
    }
}

@Composable
fun ComoriOdApp(context: Context) {
    val signInModel: GoogleSignInModel = viewModel(factory = GoogleSignInModelFactory(context))
    val jwtUtils = JWTUtils()

    ComoriODTheme {
        val navController = rememberNavController()
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                AppBar(onMenuClicked = {
                    coroutineScope.launch {
                        if (scaffoldState.drawerState.isClosed)
                            scaffoldState.drawerState.open()
                        else
                            scaffoldState.drawerState.close()
                    }
                })
            },
            drawerContent = { Drawer(context) },
            scaffoldState = scaffoldState,
            bottomBar = { BottomNavigation(navController = navController) }) {
            BottomBarMain(navController = navController, jwtUtils, signInModel)
        }
    }
}

@Composable
fun BottomBarMain(
    navController: NavHostController,
    jwtUtils: JWTUtils,
    signInModel: GoogleSignInModel,
) {
    NavHost(navController, startDestination = Screen.Library.route) {
        composable(Screen.Library.route) {
            LibraryScreen(jwtUtils, signInModel)
        }

        composable(Screen.Search.route) {
            SearchScreen()
        }

        composable(Screen.Favourites.route) {
            FavouritesScreen()
        }
    }
}
