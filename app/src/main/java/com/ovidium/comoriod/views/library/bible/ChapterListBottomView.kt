package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ovidium.comoriod.data.bible.BibleBook
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.library.StateHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChapterListBottomView(
    libraryModel: LibraryModel,
    currentBibleBook: BibleBook,
    scope: CoroutineScope,
    pagerState: PagerState,
    showChapterListBottomSheet: MutableState<Boolean>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "${currentBibleBook.name} - ${currentBibleBook.chapters} capitole",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = getNamedColor("Text", isSystemInDarkTheme())
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(currentBibleBook.chapters) { chapter ->
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .border(1.dp, getNamedColor("Link", isSystemInDarkTheme()), CircleShape)
                        .height(40.dp)
                        .clickable {
                            scope.launch {
                                libraryModel.getBibleChapter(currentBibleBook.name, chapter)
                                pagerState.scrollToPage(chapter)
                            }.invokeOnCompletion { showChapterListBottomSheet.value = false }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = (chapter + 1).toString(),
                        color = getNamedColor("Text", isSystemInDarkTheme())
                    )
                }
            }
        }

    }
}