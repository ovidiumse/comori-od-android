package com.ovidium.comoriod

import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ovidium.comoriod.components.AppBar
import com.ovidium.comoriod.components.Drawer
import com.ovidium.comoriod.model.*
import com.ovidium.comoriod.ui.theme.ComoriODTheme
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.views.FavoritesScreen
import com.ovidium.comoriod.views.library.LibraryScreen
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.article.ArticleView
import com.ovidium.comoriod.views.library.books.BooksForAuthorScreen
import com.ovidium.comoriod.views.library.books.BooksForVolumeScreen
import com.ovidium.comoriod.views.library.authors.TitlesForAuthorScreen
import com.ovidium.comoriod.views.library.books.BookScreen
import com.ovidium.comoriod.views.library.volumes.VolumesForAuthorScreen
import com.ovidium.comoriod.views.markups.MarkupsScreen
import com.ovidium.comoriod.views.search.SearchScreen
import kotlinx.coroutines.CoroutineScope
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
    val isDark = isSystemInDarkTheme()

    val signInModel: GoogleSignInModel = viewModel(factory = GoogleSignInModelFactory(context))
    val jwtUtils = JWTUtils()

    ComoriODTheme {

        val navController = rememberNavController()
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val coroutineScope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        Scaffold(
            topBar = {
                when (navBackStackEntry?.destination?.route?.replaceAfter("/", "")
                    ?.replace("/", "")) {
                    Screens.Book.route -> {}
                    Screens.Search.route -> {}
                    Screens.Favorites.route -> {}
                    Screens.Markups.route -> {}
                    else -> {
                        AppBar(
                            showTitle = true,
                            onMenuClicked = {
                                launchMenu(coroutineScope, scaffoldState)
                            },
                            actions = @Composable {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.clickable(onClick = {
                                        navController.navigate(Screens.Search.route)
                                    }),
                                    tint = getNamedColor("HeaderText", isDark = isDark)
                                )
                            })
                    }
                }
            },
            drawerContent = { Drawer(context, scaffoldState.drawerState, navController) },
            scaffoldState = scaffoldState,
        ) {
            BottomBarMain(
                navController = navController,
                scaffoldState = scaffoldState,
                jwtUtils = jwtUtils,
                signInModel = signInModel,
                libraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel)),
                favoritesModel = viewModel(factory = FavoritesModelFactory(jwtUtils, signInModel)),
                searchModel = viewModel(),
                markupsModel = viewModel(factory = MarkupsModelFactory(jwtUtils, signInModel))
            )
        }
    }
}


@Composable
fun BottomBarMain(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    jwtUtils: JWTUtils,
    signInModel: GoogleSignInModel,
    libraryModel: LibraryModel,
    favoritesModel: FavoritesModel,
    searchModel: SearchModel,
    markupsModel: MarkupsModel
) {

    NavHost(navController, startDestination = Screens.Library.route) {
        composable(Screens.Library.route) {
            LibraryScreen(navController, signInModel, libraryModel)
        }

        composable(Screens.Search.route) {
            SearchScreen(navController, scaffoldState = scaffoldState, searchModel)
        }

        composable(Screens.Favorites.route) {
            FavoritesScreen(navController, favoritesModel, scaffoldState)
        }

        composable(Screens.Markups.route) {
            MarkupsScreen(navController, markupsModel, scaffoldState)
        }

        composable(
            route = Screens.Article.route + "/{articleID}?markupId={markupId}&isSearch={isSearch}",
            arguments = listOf(
                navArgument("articleID") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("markupId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("isSearch") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            fun getArticleID(): String {
                return if (entry.arguments == null)
                    ""
                else
                    entry.arguments!!.getString("articleID", "")
            }

            fun getMarkupId(): String? {
                return entry.arguments?.getString("markupId")
            }

            fun isSearch(): String? {
                return entry.arguments?.getString("isSearch")
            }

            ArticleView(
                getArticleID(),
                getMarkupId(),
                isSearch(),
                signInModel,
                favoritesModel,
                searchModel,
                markupsModel
            )
        }

        composable(
            route = Screens.Book.route + "/{book}",
            arguments = listOf(navArgument("book") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { entry ->
            fun getBook(): String {
                return if (entry.arguments == null)
                    ""
                else
                    entry.arguments!!.getString("book", "")
            }

            BookScreen(
                book = getBook(),
                scaffoldState = scaffoldState,
                jwtUtils = jwtUtils,
                signInModel = signInModel,
                favoritesModel = favoritesModel,
                searchModel = searchModel,
                markupsModel = markupsModel
            )
        }

        composable(
            route = Screens.BooksForVolume.route + "/{volume}",
            arguments = listOf(navArgument("volume") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { entry ->
            fun getVolume(): String {
                return if (entry.arguments == null)
                    ""
                else
                    entry.arguments!!.getString("volume", "")
            }

            BooksForVolumeScreen(
                navController = navController,
                volume = getVolume(),
                jwtUtils = jwtUtils,
                signInModel = signInModel
            )
        }


        composable(
            route = Screens.BooksForAuthor.route + "/{author}",
            arguments = listOf(navArgument("author") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { entry ->
            fun getAuthor(): String {
                return if (entry.arguments == null)
                    ""
                else
                    entry.arguments!!.getString("author", "")
            }

            BooksForAuthorScreen(
                navController = navController,
                author = getAuthor(),
                jwtUtils = jwtUtils,
                signInModel = signInModel
            )
        }

        composable(
            route = Screens.VolumesForAuthor.route + "/{author}",
            arguments = listOf(navArgument("author") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { entry ->
            fun getAuthor(): String {
                return if (entry.arguments == null)
                    ""
                else
                    entry.arguments!!.getString("author", "")
            }
            VolumesForAuthorScreen(
                navController = navController,
                author = getAuthor(),
                jwtUtils = jwtUtils,
                signInModel = signInModel
            )
        }


        composable(
            route = Screens.PoemsForAuthor.route + "/{author}",
            arguments = listOf(navArgument("author") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { entry ->
            fun getAuthor(): String {
                return if (entry.arguments == null)
                    ""
                else
                    entry.arguments!!.getString("author", "")
            }

            TitlesForAuthorScreen(
                navController = navController,
                libraryModel = libraryModel,
                scaffoldState = scaffoldState,
                params = mapOf("authors" to getAuthor(), "types" to "poezie")
            )
        }

        composable(
            route = Screens.ArticlesForAuthor.route + "/{author}",
            arguments = listOf(navArgument("author") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { entry ->
            fun getAuthor(): String {
                return if (entry.arguments == null)
                    ""
                else
                    entry.arguments!!.getString("author", "")
            }
            TitlesForAuthorScreen(
                navController = navController,
                libraryModel = libraryModel,
                scaffoldState = scaffoldState,
                params = mapOf("authors" to getAuthor(), "types" to "articol")
            )
        }


    }
}


fun launchMenu(coroutineScope: CoroutineScope, scaffoldState: ScaffoldState) {
    coroutineScope.launch {
        if (scaffoldState.drawerState.isClosed)
            scaffoldState.drawerState.open()
        else
            scaffoldState.drawerState.close()
    }
}