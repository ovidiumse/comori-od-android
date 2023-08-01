package com.ovidium.comoriod

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ovidium.comoriod.api.RetrofitBuilder
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
import java.net.URLDecoder


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitBuilder.appContext = applicationContext
        setContent {
            ComoriOdApp(applicationContext)
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(getNamedColor("HeaderBar", isSystemInDarkTheme()))
        }

    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ComoriOdApp(context: Context) {
    val isDark = isSystemInDarkTheme()

    val signInModel: GoogleSignInModel = viewModel(factory = GoogleSignInModelFactory(context))
    val jwtUtils = JWTUtils()
    val libraryModel: LibraryModel = viewModel(factory = LibraryModelFactory(jwtUtils, signInModel))

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
                            onMenuClicked = {
                                launchMenu(coroutineScope, scaffoldState.drawerState)
                            },
                            onTitleClicked = {
                                navController.navigate(Screens.Library.route) {
                                    launchSingleTop = true
                                }
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
                libraryModel = libraryModel,
                favoritesModel = viewModel(factory = FavoritesModelFactory(jwtUtils, signInModel)),
                searchModel = viewModel(),
                markupsModel = viewModel(factory = MarkupsModelFactory(jwtUtils, signInModel)),
                readArticlesModel = viewModel(
                    factory = ReadArticlesModelFactory(jwtUtils, signInModel, libraryModel)
                )
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
    markupsModel: MarkupsModel,
    readArticlesModel: ReadArticlesModel
) {

    NavHost(navController, startDestination = Screens.Library.route) {
        composable(Screens.Library.route) {
            LibraryScreen(navController, signInModel, libraryModel)
        }

        composable(Screens.Search.route) {
            SearchScreen(navController, scaffoldState = scaffoldState, searchModel)
        }

        composable(Screens.Favorites.route) {
            FavoritesScreen(navController, favoritesModel, signInModel, scaffoldState)
        }

        composable(Screens.Markups.route) {
            MarkupsScreen(navController, markupsModel, signInModel, scaffoldState)
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
                    URLDecoder.decode(entry.arguments!!.getString("articleID", ""), "utf-8")
            }

            fun getMarkupId(): String? {
                return entry.arguments?.getString("markupId")
                    ?.let { markupId -> URLDecoder.decode(markupId, "utf-8") }
            }

            fun isSearch(): String? {
                return entry.arguments?.getString("isSearch")
                    ?.let { isSearch -> URLDecoder.decode(isSearch, "utf-8") }
            }

            ArticleView(
                getArticleID(),
                getMarkupId(),
                isSearch(),
                signInModel,
                favoritesModel,
                searchModel,
                markupsModel,
                readArticlesModel
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
                    URLDecoder.decode(entry.arguments!!.getString("book", ""), "utf-8")
            }

            BookScreen(
                book = getBook(),
                scaffoldState = scaffoldState,
                jwtUtils = jwtUtils,
                signInModel = signInModel,
                favoritesModel = favoritesModel,
                searchModel = searchModel,
                markupsModel = markupsModel,
                readArticlesModel = readArticlesModel,
                navController = navController
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
                    URLDecoder.decode(entry.arguments!!.getString("volume", ""), "utf-8")
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
                    URLDecoder.decode(entry.arguments!!.getString("author", ""), "utf-8")
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
                    URLDecoder.decode(entry.arguments!!.getString("author", ""), "utf-8")
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
                    URLDecoder.decode(entry.arguments!!.getString("author", ""), "utf-8")
            }

            TitlesForAuthorScreen(
                navController = navController,
                libraryModel = libraryModel,
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
                    URLDecoder.decode(entry.arguments!!.getString("author", ""), "utf-8")
            }
            TitlesForAuthorScreen(
                navController = navController,
                libraryModel = libraryModel,
                params = mapOf("authors" to getAuthor(), "types" to "articol")
            )
        }


    }
}


fun launchMenu(coroutineScope: CoroutineScope, drawerState: DrawerState) {
    coroutineScope.launch {
        if (drawerState.isClosed)
            drawerState.open()
        else
            drawerState.close()
    }
}