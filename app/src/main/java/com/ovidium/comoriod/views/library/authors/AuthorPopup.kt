package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.colors
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.articulate
import com.ovidium.comoriod.views.Screens

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
        val isDark = isSystemInDarkTheme()

        Box(
            Modifier
                .background(
                    getNamedColor("Alice", isDark = isDark),
                    RoundedCornerShape(16.dp)
                )
                .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.End,
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
                        painter = rememberAsyncImagePainter(authorInfo.photo_url_sm),
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
                        if (authorInfo.dob != null)
                            Text(
                                text = authorInfo.dob + " - " + (authorInfo.dod?: ""),
                                color = colors.colorSecondaryText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light
                            )
                    }
                }
                Column {
                    if (authorInfo.shortAbout != null)
                        Text(
                            text = authorInfo.shortAbout,
                            color = colors.colorSecondaryText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier
                                .padding(bottom = 24.dp)
                        )

                    Row {
                        val bookCnt = getBooksCnt(authorInfo)
                        if (bookCnt > 0) {
                            Text(
                                text = fmtBooksNumber(bookCnt),
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
                        }

                        val volumeCnt = getVolumesCnt(authorInfo)
                        if (volumeCnt > 0) {
                            Text(
                                text = fmtVolumesNumber(volumeCnt),
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
                        }
                        if (getPoemsCnt(authorInfo) > 0) {
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
                        }
                        if (getArticlesCnt(authorInfo) > 0) {
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
}

fun getBooksCnt(authorBucket: Bucket): Int {
    return authorBucket.books.buckets.size
}

fun fmtBooksNumber(bookCnt: Int): String {
    return articulate(bookCnt, "cărți", "carte")
}

fun getVolumesCnt(authorBucket: Bucket): Int {
    return authorBucket.volumes.buckets.size
}

fun fmtVolumesNumber(volumeCnt: Int): String {
    return articulate(volumeCnt, "volume", "volum")
}

fun getPoemsCnt(authorBucket: Bucket): Int {
    val poems = authorBucket.types.buckets.filter { type -> type.key == "poezie" }
    if (poems.isEmpty())
        return 0

    return poems.first().doc_count
}

fun getPoemsNumber(authorBucket: Bucket): String {
    return articulate(getPoemsCnt(authorBucket), "poezii", "poezie", isShort = true)
}

fun getArticlesCnt(authorBucket: Bucket): Int {
    val articles = authorBucket.types.buckets.filter { type -> type.key == "articol" }
    if (articles.isEmpty())
        return 0

    return articles.first().doc_count
}

fun getArticlesNumber(authorBucket: Bucket): String {
    return articulate(getArticlesCnt(authorBucket), "articole", "articol", isShort = true)
}