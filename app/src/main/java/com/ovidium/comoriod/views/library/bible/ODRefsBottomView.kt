package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedText
import com.ovidium.comoriod.data.bible.ODRef
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ODRefsBottomView(
    state: Resource<BibleChapter?>,
    libraryModel: LibraryModel,
    currentAuthor: MutableState<CountedAuthor?>,
    odReferenceData: Map<String, ODRef>,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(16.dp)
            .fillMaxSize()
    ) {
        state.data?.let { bibleChapter ->
            libraryModel.currentVerseData.value?.let { currentVerse ->
                ODRefsBottomSheetAuthorCarousel(
                    libraryModel,
                    currentVerse,
                    bibleChapter,
                    currentAuthor,
                    isSystemInDarkTheme(),
                    {  }
                )
                Text(
                    text = odReferenceData.values.first().text
                )
            }
        }
    }
}