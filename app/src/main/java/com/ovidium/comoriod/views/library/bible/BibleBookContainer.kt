package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedText
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BibleBookContainer(
    bibleChapterData: BibleChapter?,
    scope: CoroutineScope,
    showBottomSheet: MutableState<Boolean>,
    libraryModel: LibraryModel,
    navController: NavHostController
) {
    val isDarkTheme = isSystemInDarkTheme()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp, end = 16.dp, top = 16.dp
            )
    ) {
        bibleChapterData?.getFormatedText(isDarkTheme)?.let {
            itemsIndexed(it) { _, bibleVerseContent ->
                Text(
                    text = bibleVerseContent.formatedVerse,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                if (bibleVerseContent.formatedReference.text.isNotEmpty())
                    ClickableText(
                        text = bibleVerseContent.formatedReference,
                        modifier = Modifier
                            .padding(bottom = 5.dp),
                        style = TextStyle(lineHeight = 20.sp)
                    ) { offset ->

                        val annotation = bibleVerseContent.formatedReference.getStringAnnotations(
                            tag = "URL",
                            start = offset,
                            end = offset
                        ).firstOrNull()
                        scope.launch {
                            libraryModel.getFullVerseReferenceData(bibleChapterData, annotation!!.item, isDarkTheme)
                            if (!showBottomSheet.value) {
                                showBottomSheet.value = true
                            }
                        }
                    }
                val odRefsForVerse = bibleChapterData.odRefs?.filter { bibleVerseContent.odReferences.contains(it.key) }
                val dict = odRefsForVerse!!.map { it.value.author }
                    .groupingBy { it }
                    .eachCount()
                val tempArray = dict.map { (name, count) ->
                    CountedAuthor(name = name, count = count)
                }
                val sortedAuthors = tempArray.sortedWith(compareByDescending<CountedAuthor> { it.count }
                    .thenBy { it.name })
                Row {
                    sortedAuthors.forEach() { author ->
                        val photoURL = odRefsForVerse.values.first { it.author == author.name }.authorPhotoURLSm
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                        ) {
                            if (!photoURL.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(photoURL),
                                    contentDescription = "bible authors",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .requiredSize(35.dp)
                                        .border(1.dp, getNamedColor("Link", isDarkTheme), RoundedCornerShape(100))
                                        .clip(RoundedCornerShape(100)),
                                    colorFilter = ColorFilter.colorMatrix(
                                        ColorMatrix().apply { setToSaturation(0f) }
                                    )
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .background(Color(0xffc9d1d9), RoundedCornerShape(100))
                                        .height(35.dp)
                                        .width(35.dp)
                                        .alpha(0.5f)
                                        .border(1.dp, getNamedColor("Link", isDarkTheme), RoundedCornerShape(100))
                                ) {
                                    val authorInitials = Regex("\\b(\\w)\\w*")
                                        .findAll(author.name)
                                        .map { it.groupValues[1].uppercase() }
                                        .joinToString("")
                                    Text(authorInitials)
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .background(getNamedColor("Link", isDarkTheme), RoundedCornerShape(100))
                                    .height(20.dp)
                                    .width(20.dp)
                            ) {
                                Text(
                                    text = "${author.count}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


data class CountedAuthor(val name: String, val count: Int)