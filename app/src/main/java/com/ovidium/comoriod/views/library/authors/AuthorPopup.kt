package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.ui.theme.colors
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.articulate
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.search.filter.FilterCategoryView
import com.ovidium.comoriod.views.search.filter.FilterViewTopBar
import kotlinx.coroutines.launch

@Composable
fun AuthorPopup(
    navController: NavController,
    authorInfo: Bucket,
    libraryModel: LibraryModel,
    onExitAction: () -> Unit,
) {

    Popup(
        alignment = Alignment.Center,
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp / 2
        val screenWidth = configuration.screenWidthDp.dp - 40.dp
        val isDark = isSystemInDarkTheme()

        Box(
            Modifier
                .size(screenWidth, screenHeight)
                .background(
                    getNamedColor("Alice", isDark = isDark),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = getNamedColor("Link", isDark = isDark),
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp)
                            .clickable { onExitAction() }
                    )
                }
                Row(
                    modifier = Modifier.padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(getDrawableByAuthor(authorInfo.name)),
                        contentDescription = "details",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .requiredSize(120.dp)
                            .clip(RoundedCornerShape(100))
                    )
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = authorInfo.name,
                            color = colors.colorSecondaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = authorInfo.dob + " - " + authorInfo.dod,
                            color = colors.colorSecondaryText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
                Column {
                    Text(
                        text = authorInfo.shortAbout,
                        color = colors.colorSecondaryText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                    )
                    Row {
                        Text(
                            text = getBooksNumber(authorInfo),
                            color = colors.colorSecondaryText,
                            fontSize = 11.sp,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    getNamedColor("CardButton", isDark = isDark),
                                    RoundedCornerShape(50)
                                )
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate(
                                        Screens.BooksForAuthor.withArgs(
                                            authorInfo.name
                                        )
                                    )
                                }
                        )
                        Text(
                            text = getVolumesNumber(authorInfo),
                            color = colors.colorSecondaryText,
                            fontSize = 11.sp,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    getNamedColor("CardButton", isDark = isDark),
                                    RoundedCornerShape(50)
                                )
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate(
                                        Screens.VolumesForAuthor.withArgs(
                                            authorInfo.name
                                        )
                                    )
                                }
                        )
                        Text(
                            text = getPoemsNumber(authorInfo),
                            color = colors.colorSecondaryText,
                            fontSize = 11.sp,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    getNamedColor("CardButton", isDark = isDark),
                                    RoundedCornerShape(50)
                                )
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate(
                                        Screens.PoemsForAuthor.withArgs(
                                            authorInfo.name
                                        )
                                    )
                                    libraryModel.getTitles(
                                        params = mapOf(
                                            "authors" to authorInfo.name,
                                            "types" to "poezie"
                                        )
                                    )
                                }
                        )
                        Text(
                            text = getArticlesNumber(authorInfo),
                            color = colors.colorSecondaryText,
                            fontSize = 11.sp,
                            maxLines = 1,
                            modifier = Modifier
                                .background(
                                    getNamedColor("CardButton", isDark = isDark),
                                    RoundedCornerShape(50)
                                )
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate(
                                        Screens.ArticlesForAuthor.withArgs(
                                            authorInfo.name
                                        )
                                    )
                                    libraryModel.getTitles(
                                        params = mapOf(
                                            "authors" to authorInfo.name,
                                            "types" to "articol"
                                        )
                                    )
                                }
                        )
                    }
                }
            }
        }
    }
}

fun getBooksNumber(authorBucket: Bucket): String {
    return articulate(authorBucket.books.buckets.size, "cărți", "carte")
}

fun getVolumesNumber(authorBucket: Bucket): String {
    return articulate(authorBucket.volumes.buckets.size, "volume", "volum")
}

fun getPoemsNumber(authorBucket: Bucket): String {
    val poems =
        authorBucket.types.buckets.filter({ type -> type.key == "poezie" }).first().doc_count
    return articulate(poems, "poezii", "poezie", isShort = true)
}

fun getArticlesNumber(authorBucket: Bucket): String {
    val articles =
        authorBucket.types.buckets.filter({ type -> type.key == "articol" }).first().doc_count
    return articulate(articles, "articole", "articol", isShort = true)
}