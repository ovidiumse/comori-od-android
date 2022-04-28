package com.ovidium.comoriod

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.ovidium.comoriod.views.FavouritesScreen
import com.ovidium.comoriod.views.LibraryScreen
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.article.ArticleView
import com.ovidium.comoriod.views.search.SearchScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


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
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        Scaffold(
            topBar = {
                if (navBackStackEntry?.destination?.route != Screens.Search.route) {
                    AppBar(
                        onMenuClicked = {
                            launchMenu(coroutineScope, scaffoldState)
                        },
                        onSearchClicked = {
                            navController.navigate(Screens.Search.route)
                        })
                }
            },
            drawerContent = { Drawer(context) },
            scaffoldState = scaffoldState,
            bottomBar = {
                BottomNavigation {
                    val currentDestination = navBackStackEntry?.destination
                    Screens.values().forEach { screen ->
                        if (screen.isMenu) {
                            BottomNavigationItem(
                                icon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = screen.icon),
                                        contentDescription = screen.title
                                    )
                                },
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
            }
        ) { _ ->
            BottomBarMain(navController = navController, jwtUtils, signInModel)
        }
    }
}


@Composable
fun BottomBarMain(
    navController: NavHostController,
    jwtUtils: JWTUtils,
    signInModel: GoogleSignInModel
) {
    NavHost(navController, startDestination = Screens.Library.route) {
        composable(Screens.Library.route) {
            LibraryScreen(jwtUtils, signInModel)
        }

        composable(Screens.Search.route) {
            SearchScreen(navController)
        }

        composable(Screens.Favourites.route) {
            FavouritesScreen()
        }

        composable(
            route = Screens.Article.route + "/{articleID}",
            arguments = listOf(navArgument("articleID") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { entry ->
            fun getArticleID(): String {
                return if (entry.arguments == null)
                    ""
                else
                    entry.arguments!!.getString("articleID", "")
            }

            ArticleView(articleID = getArticleID())
        }

//        composable(
//            route = Screens.Filter.route + "/{query}",
//            arguments = listOf(navArgument("query") {
//                type = NavType.StringType
//                defaultValue = ""
//                nullable = true
//            })
//        ) { entry ->
//            fun getQuery(): String {
//                return if (entry.arguments == null)
//                    ""
//                else
//                    entry.arguments!!.getString("query", "")
//            }
//            FilterView(query = getQuery())
//        }
    }
}


private fun launchMenu(coroutineScope: CoroutineScope, scaffoldState: ScaffoldState) {
    coroutineScope.launch {
        if (scaffoldState.drawerState.isClosed)
            scaffoldState.drawerState.open()
        else
            scaffoldState.drawerState.close()
    }
}