package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleVerse
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import kotlinx.coroutines.CoroutineScope

@Composable
fun VersesAuthorCarousel(
    libraryModel: LibraryModel,
    bibleVerseContent: BibleVerse,
    bibleChapterData: BibleChapter,
    isDarkTheme: Boolean,
    scope: CoroutineScope,
    showOdRefBottomSheet: MutableState<Boolean>,
    onClick: (CountedAuthor) -> Unit
) {
    Row {
        libraryModel.getSortedAuthors(bibleVerseContent, bibleChapterData).forEach { author ->
            val photoURL = libraryModel.getAuthorImageUrl(bibleVerseContent, bibleChapterData, author.name)
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(bottom = 12.dp)
            ) {
                if (!photoURL.isNullOrEmpty()) {
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .requiredSize(35.dp)
                            .border(1.dp, getNamedColor("Link", isDarkTheme), RoundedCornerShape(100))
                            .clip(RoundedCornerShape(100))
                            .clickable {
                                onClick(author)
                            },
                        painter = rememberAsyncImagePainter(photoURL),
                        contentDescription = "bible authors",
                        contentScale = ContentScale.FillBounds,
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
                            .clickable {
                                onClick(author)
                            },
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
                        .height(13.dp)
                        .width(13.dp)
                ) {
                    Text(
                        text = "${author.count}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = getNamedColor("InvertedText", isDarkTheme)
                    )
                }
            }
        }
    }
}