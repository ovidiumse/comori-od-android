package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedText
import com.ovidium.comoriod.model.LibraryModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BibleBookContainer(
    bibleChapterData: BibleChapter?,
    scope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    scaffoldState: ScaffoldState,
    libraryModel: LibraryModel,
    navController: NavHostController
) {
    val isDarkTheme = isSystemInDarkTheme()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(
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
                        modifier = Modifier.padding(bottom = 5.dp),
                        style = TextStyle(lineHeight = 20.sp)
                    ) { offset ->

                        val annotation = bibleVerseContent.formatedReference.getStringAnnotations(
                            tag = "URL",
                            start = offset,
                            end = offset
                        ).firstOrNull()

                        // TODO show the Partial bottom sheet with ref verses
                        println("Clicked: ${annotation!!.item}")


                        scope.launch {
                            libraryModel.getFullVerseReferenceData(bibleChapterData, annotation.item, isDarkTheme)
                            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
            }
        }
    }
}