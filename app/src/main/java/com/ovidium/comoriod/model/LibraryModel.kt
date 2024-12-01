package com.ovidium.comoriod.model

import android.util.Log
import androidx.annotation.Keep
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.LibraryDataSource
import com.ovidium.comoriod.data.bible.BibleChapter
import com.ovidium.comoriod.data.bible.BibleVerse
import com.ovidium.comoriod.data.bible.ContentType
import com.ovidium.comoriod.data.bible.Verse
import com.ovidium.comoriod.data.recommended.RecommendedResponseItem
import com.ovidium.comoriod.data.titles.TitleHit
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
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

fun BibleChapter.getFormatedText(isDarkMode: Boolean): List<BibleVerse> {

    val finalBibleVerseList = mutableListOf<BibleVerse>()

    val verses: List<Verse> = this.hits.hits.flatMap { it.source.verses }

    verses.forEach { verse ->

        // build the verse with reference symbols
        var annotatedString = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = Color.Gray,
                    fontSize = 18.sp
                )
            ) {
                append(verse.number.toString().addSuffix(DOT_SYMBOL))
            }
            append(SPACE_SYMBOL)
            verse.content.forEach { verse ->
                when (verse.type) {
                    ContentType.NORMAL -> {
                        withStyle(
                            SpanStyle(
                                fontSize = 18.sp
                            )
                        ) {
                            append(verse.text)
                        }
                    }
                    ContentType.NOTE -> {
                        withStyle(
                            SpanStyle(
                                color = getNamedColor("Link", isDarkMode),
                                fontSize = 18.sp,
                                baselineShift = BaselineShift.Superscript
                            )
                        ) {
                            append(verse.text)
                        }
                    }
                    ContentType.JESUS -> {
                        withStyle(
                            SpanStyle(
                                color = Color.Red,
                                fontSize = 18.sp
                            )
                        ) {
                            append(verse.text)
                        }
                    }
                    ContentType.REFERENCE -> { withStyle(
                        SpanStyle(
                            color = getNamedColor("markupChoc", isDarkMode),
                            fontSize = 14.sp,
                            baselineShift = BaselineShift.Superscript
                        )
                    ) {
                        append(verse.text)
                    } }
                }
            }
        }

        var annotatedRef = buildAnnotatedString {
            verse.references.oneStar?.forEach {
                withStyle(
                    SpanStyle(
                        color = getNamedColor("markupChoc", isDarkMode),
                        fontSize = 14.sp
                    )
                ) {
                    append(ONE_STAR_SYMBOL)
                }
                withAnnotation(tag = "URL", annotation = it) {
                    withStyle(
                        SpanStyle(
                            color = getNamedColor("Link", isDarkMode),
                            fontSize = 14.sp
                        )
                    ) {
                        append(it + SPACE_SYMBOL)
                    }
                }
            }

            verse.references.twoStars?.forEach {
                withStyle(
                    SpanStyle(
                        color = getNamedColor("markupChoc", isDarkMode),
                        fontSize = 14.sp
                    )
                ) {
                    append(TWO_STARS_SYMBOL)
                }
                withAnnotation(tag = "URL", annotation = it) {
                    withStyle(
                        SpanStyle(
                            color = getNamedColor("Link", isDarkMode),
                            fontSize = 14.sp
                        )
                    ) {
                        append(it + SPACE_SYMBOL)
                    }
                }
            }

            verse.references.oneCross?.forEach {
                withStyle(
                    SpanStyle(
                        color = getNamedColor("markupChoc", isDarkMode),
                        fontSize = 14.sp
                    )
                ) {
                    append(ONE_CROSS_SYMBOL)
                }
                withAnnotation(tag = "URL", annotation = it) {
                    withStyle(
                        SpanStyle(
                            color = getNamedColor("Link", isDarkMode),
                            fontSize = 14.sp
                        )
                    ) {
                        append(it + SPACE_SYMBOL)
                    }
                }
            }

            verse.references.twoCrosses?.forEach {
                withStyle(
                    SpanStyle(
                        color = getNamedColor("markupChoc", isDarkMode),
                        fontSize = 14.sp
                    )
                ) {
                    append(TWO_CROSSES_SYMBOL)
                }
                withAnnotation(tag = "URL", annotation = it) {
                    withStyle(
                        SpanStyle(
                            color = getNamedColor("Link", isDarkMode),
                            fontSize = 14.sp
                        )
                    ) {
                        append(it + SPACE_SYMBOL)
                    }
                }
            }

            verse.references.starAndCross?.forEach {
                withStyle(
                    SpanStyle(
                        color = getNamedColor("markupChoc", isDarkMode),
                        fontSize = 14.sp
                    )
                ) {
                    append(ONE_STAR_ONE_CROSS_SYMBOL)
                }
                withAnnotation(tag = "URL", annotation = it) {
                    withStyle(
                        SpanStyle(
                            color = getNamedColor("Link", isDarkMode),
                            fontSize = 14.sp
                        )
                    ) {
                        append(it + SPACE_SYMBOL)
                    }
                }
            }
        }

        finalBibleVerseList.add(
            BibleVerse(
                annotatedString,
                annotatedRef,
                emptyList()
            )
        )
    }
    return finalBibleVerseList
}

fun String.addPrefix(prefix: String) = "$prefix$this"
fun String.addSuffix(suffix: String) = "$this$suffix"

const val DOT_SYMBOL = "."
const val SPACE_SYMBOL = " "
const val NO_SYMBOL = ""
const val JESUS_SYMBOL = "+"

const val ONE_STAR_SYMBOL = "*"
const val TWO_STARS_SYMBOL = "**"
const val ONE_CROSS_SYMBOL = "†"
const val TWO_CROSSES_SYMBOL = "††"
const val ONE_STAR_ONE_CROSS_SYMBOL = "*†"