package com.ovidium.comoriod.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.ovidium.comoriod.data.article.BibleRefVerse
import com.ovidium.comoriod.utils.EventBus
import com.ovidium.comoriod.utils.RefClickedEvent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ArticleModel: ViewModel() {

    private var bibleRefs = mutableStateMapOf<String,  SnapshotStateList<BibleRefVerse>>()
    var isRegisteredForClickedRefs = mutableStateOf(false)

    fun clearBibleRefs() {
        bibleRefs.clear()
    }

    fun getBibleRefs(articleId: String): SnapshotStateList<BibleRefVerse> {
        return bibleRefs.getOrPut(articleId){ SnapshotStateList() }
    }

}