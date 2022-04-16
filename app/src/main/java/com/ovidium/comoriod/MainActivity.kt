package com.ovidium.comoriod

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.components.Drawer
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.GoogleSignInModelFactory
import com.ovidium.comoriod.ui.theme.ComoriODTheme
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.*
import com.ovidium.comoriod.views.search.SearchResultsScreen
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
        val bottomBarItems = listOf(
            Screen.Library,
            Screen.Search,
            Screen.Favourites,
        )
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
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    bottomBarItems.forEach { screen ->
                        BottomNavigationItem(
                            icon = { androidx.compose.material.Icon(
                                imageVector = ImageVector.vectorResource(id = screen.icon),
                                contentDescription = screen.title
                            ) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
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
            SearchScreen(navController)
        }

        composable(Screen.Favourites.route) {
            FavouritesScreen()
        }

        composable(
            route = SearchScreens.SearchResults.route + "/{query}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = "Unknown"
                    nullable = true
                }
            )
        ) { entry ->
            SearchResultsScreen(query = entry.arguments!!.getString("query")!!)
        }

    }
}
