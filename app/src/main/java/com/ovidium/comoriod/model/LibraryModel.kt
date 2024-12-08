package com.ovidium.comoriod.model

import android.util.Log
import androidx.annotation.Keep
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.LibraryDataSource
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleChapter.Companion.getFormatedReferencesForVerse
import com.ovidium.comoriod.data.bible.BibleVerse
import com.ovidium.comoriod.data.bible.ODRef
import com.ovidium.comoriod.data.recommended.RecommendedResponseItem
import com.ovidium.comoriod.data.titles.TitleHit
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.views.library.bible.CountedAuthor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val TAG = "AggregationsModel"

@Keep
class LibraryModel(jwtUtils: JWTUtils, signInModel: GoogleSignInModel) :
    ViewModel() {

    private val dataSource =
        LibraryDataSource(jwtUtils, RetrofitBuilder.apiService, signInModel, viewModelScope)

    val authorsData by lazy { dataSource.authorsData }
    val volumesData by lazy { dataSource.volumesData }
    val booksData by lazy { dataSource.booksData }
    val recentlyAddedBooksData by lazy { dataSource.recentlyAddedBooksData }
    val recommendedData = mutableStateOf<Resource<SnapshotStateList<RecommendedResponseItem>>>(Resource.uninitialized())
    val trendingData by lazy { dataSource.trendingData }
    val bibleBooksData by lazy { dataSource.bibleBooksData }

    private var _bibleChapterData = MutableStateFlow<Map<Pair<String, Int>, Resource<BibleChapter?>>>(emptyMap())
    val bibleChapterData = _bibleChapterData.asStateFlow()

    private var _verseFullReferenceData = MutableStateFlow(AnnotatedString(""))
    val verseFullReferenceData = _verseFullReferenceData.asStateFlow()

    private var _odReferenceData: MutableStateFlow<Map<String, ODRef>> = MutableStateFlow(mutableMapOf())
    val odReferenceData = _odReferenceData.asStateFlow()

    class TitlesData {
        var totalHitsCnt = mutableStateOf(0)
        var titles = mutableStateListOf<TitleHit>()
    }

    var titlesData = mutableStateOf<Resource<TitlesData>>(Resource.uninitialized())

    private fun handleResponse(offset: Int, response: Resource<TitlesResponse>) {
        when (response.status) {
            Status.SUCCESS -> {
                if (offset == 0) {
                    titlesData.value = Resource.success(TitlesData())
                    response.data?.hits?.total?.value?.let { hitCnt ->
                        titlesData.value.data?.totalHitsCnt?.value = hitCnt
                    }
                }

                response.data?.hits?.hits?.forEach { hit ->
                    titlesData.value.data?.titles?.add(hit)
                }
            }

            Status.LOADING -> titlesData.value = Resource.loading(null)
            Status.ERROR -> titlesData.value = Resource.error(null, response.message)
            Status.UNINITIALIZED -> titlesData.value = Resource.uninitialized()
        }
    }

    fun getTitles(bookTitle: String) {
        viewModelScope.launch {
            dataSource.getTitles(limit = 10000, params = mapOf("books" to bookTitle))
                .collectLatest { response ->
                    handleResponse(0, response)
                }
        }
    }

    fun getTitles(
        limit: Int = 20, offset: Int = 0, params: Map<String, String> = emptyMap()
    ) {
        viewModelScope.launch {
            dataSource.getTitles(offset = offset, limit = limit, params = params)
                .collectLatest { response ->
                    handleResponse(offset, response)
                }
        }
    }

    fun loadRecommended() {
        Log.d("LibraryModel", "Loading recommended articles...")
        recommendedData.value = Resource.loading(null)

        viewModelScope.launch {
            dataSource.getRecommended().collectLatest { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        recommendedData.value = Resource.success(mutableStateListOf())
                        response.data?.forEach { item ->
                            recommendedData.value.data?.add(item)
                        }
                    }

                    Status.LOADING -> {
                        recommendedData.value = Resource.loading(null)
                    }

                    Status.ERROR -> recommendedData.value = Resource.error(null, response.message)
                    Status.UNINITIALIZED -> recommendedData.value = Resource.uninitialized()
                }
            }
        }
    }

    fun getBibleChapter(
        bibleBookTitle: String,
        bibleChapterNumber: Int
    ) {
        val bibleChapterData = Pair(bibleBookTitle, bibleChapterNumber)

        if (_bibleChapterData.value.containsKey(bibleChapterData)
            && _bibleChapterData.value[bibleChapterData]?.status == Status.SUCCESS
        ) return

        viewModelScope.launch {
            dataSource.getBibleChapterData(bibleBookTitle, bibleChapterNumber).collectLatest { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        _bibleChapterData.value = _bibleChapterData.value.toMutableMap().apply {
                            remove(bibleChapterData)
                            put(bibleChapterData, Resource.success(response.data))
                        }
                    }

                    Status.ERROR -> {
                        _bibleChapterData.value = _bibleChapterData.value.toMutableMap().apply {
                            remove(bibleChapterData)
                            put(bibleChapterData, Resource.error(null, response.message))
                        }
                    }

                    Status.LOADING -> {
                        _bibleChapterData.value = _bibleChapterData.value.toMutableMap().apply {
                            remove(bibleChapterData)
                            put(bibleChapterData, Resource.loading(null))
                        }
                    }

                    Status.UNINITIALIZED -> {
                        _bibleChapterData.value = _bibleChapterData.value.toMutableMap().apply {
                            remove(bibleChapterData)
                            put(bibleChapterData, Resource.uninitialized())
                        }
                    }
                }
            }
        }
    }

    fun getFullVerseReferenceData(bibleChapterData: BibleChapter, verse: String, isDarkTheme: Boolean) {
        _verseFullReferenceData.value = bibleChapterData.getFormatedReferencesForVerse(verse, isDarkTheme)
    }

    fun getOdRefData(bibleVerseContent: BibleVerse, bibleChapterData: BibleChapter) {
        _odReferenceData.value = getOdRefsForVerse(bibleVerseContent, bibleChapterData)
    }


    fun getSortedAuthors(bibleVerseContent: BibleVerse, bibleChapterData: BibleChapter): List<CountedAuthor> {
        val tempArray = getNumberOfEachAuthorOdRefForVerse(bibleVerseContent, bibleChapterData)
            .map { (name, count) ->
                CountedAuthor(name = name, count = count)
            }
        return tempArray.sortedWith(compareByDescending<CountedAuthor> { it.count }
            .thenBy { it.name })
    }

    fun getAuthorImageUrl(bibleVerseContent: BibleVerse, bibleChapterData: BibleChapter, author: String): String? =
        getOdRefsForVerse(
            bibleVerseContent,
            bibleChapterData
        ).values.first { it.author == author }.authorPhotoURLSm

    private fun getNumberOfEachAuthorOdRefForVerse(bibleVerseContent: BibleVerse, bibleChapterData: BibleChapter): Map<String, Int> =
        getOdRefsForVerse(bibleVerseContent, bibleChapterData).map { it.value.author }
            .groupingBy { it }
            .eachCount()

    private fun getOdRefsForVerse(bibleVerseContent: BibleVerse, bibleChapterData: BibleChapter): Map<String, ODRef> =
        bibleChapterData.odRefs?.filter { bibleVerseContent.odReferences.contains(it.key) } ?: emptyMap()

}

class LibraryModelFactory(
    private val jwtUtils: JWTUtils,
    private val signInModel: GoogleSignInModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(JWTUtils::class.java, GoogleSignInModel::class.java)
            .newInstance(jwtUtils, signInModel) as T
    }
}