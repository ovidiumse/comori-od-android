package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import coil.compose.rememberAsyncImagePainter
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedText
import com.ovidium.comoriod.data.bible.BibleVerse
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BibleBookContainer(
    bibleChapterData: BibleChapter?,
    scope: CoroutineScope,
    showVerseRefBottomSheet: MutableState<Boolean>,
    showOdRefBottomSheet: MutableState<Boolean>,
    currentAuthor: MutableState<CountedAuthor?>,
    libraryModel: LibraryModel
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
                            if (!showVerseRefBottomSheet.value) {
                                showVerseRefBottomSheet.value = true
                            }
                        }
                    }

                VersesAuthorCarousel(
                    libraryModel,
                    bibleVerseContent,
                    bibleChapterData,
                    isDarkTheme,
                    scope,
                    showOdRefBottomSheet,
                    { author ->
                        scope.launch {
                        libraryModel.getOdRefData(bibleVerseContent, bibleChapterData)
                            currentAuthor.value = author
                            if (!showOdRefBottomSheet.value) {
                                showOdRefBottomSheet.value = true
                            }
                        }
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp)
                )
            }
        }
    }
}


data class CountedAuthor(val name: String, val count: Int)