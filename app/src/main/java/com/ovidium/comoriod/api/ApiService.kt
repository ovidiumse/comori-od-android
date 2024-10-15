package com.ovidium.comoriod.api

import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.article.ReadArticle
import com.ovidium.comoriod.data.article.SearchArticleResponse
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
import com.ovidium.comoriod.data.bible.BibleBooks
import com.ovidium.comoriod.data.books.BooksResponse
import com.ovidium.comoriod.data.favorites.FavoriteArticle
import com.ovidium.comoriod.data.markups.Markup
import com.ovidium.comoriod.data.recentlyaddedbooks.RecentlyAddedBooksResponse
import com.ovidium.comoriod.data.recommended.RecommendedResponse
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.data.titles.TitlesResponse
import com.ovidium.comoriod.data.trending.TrendingResponse
import com.ovidium.comoriod.data.types.TypesResponse
import com.ovidium.comoriod.data.volumes.VolumesResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    @GET("od/types")
    suspend fun getTypes(
        @Query("q") q: String = "",
        @Query("sep") sep: String = "|",
        @QueryMap params: Map<String, String> = emptyMap()
    ): TypesResponse

    @GET("od/authors")
    suspend fun getAuthors(
        @Query("q") q: String = "",
        @Query("sep") sep: String = "|",
        @QueryMap params: Map<String, String> = emptyMap()
    ): AuthorsResponse

    @GET("od/volumes")
    suspend fun getVolumes(
        @Query("q") q: String = "",
        @Query("sep") sep: String = "|",
        @QueryMap params: Map<String, String> = emptyMap()
    ): VolumesResponse

    @GET("od/books")
    suspend fun getBooks(
        @Query("q") q: String = "",
        @Query("sep") sep: String = "|",
        @QueryMap params: Map<String, String> = emptyMap()
    ): BooksResponse

    @GET("od/recentlyaddedbooks")
    suspend fun getRecentlyAddedBooks(): RecentlyAddedBooksResponse

    @GET("od/recommended")
    suspend fun getRecommended(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 10
    ): RecommendedResponse

    @GET("od/trendingarticles")
    suspend fun getTrending(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 10
    ): TrendingResponse

    @GET("od/titles/completion")
    suspend fun autocomplete(@Query("prefix") prefix: String): AutocompleteResponse

    @GET("od/articles")
    suspend fun search(
        @Query("q") q: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sep") sep: String = "|",
        @QueryMap params: Map<String, String> = emptyMap()
    ): SearchResponse

    @GET("od/data/{id}.json")
    suspend fun getArticle(
        @Path("id") id: String
    ): ArticleResponse

    @GET("od/articles")
    suspend fun getSearchArticle(
        @Query("id") id: String,
        @Query("highlight") searchTerm: String
    ): SearchArticleResponse

    @GET("od/titles")
    suspend fun getTitles(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sep") sep: String = "|",
        @QueryMap params: Map<String, String> = emptyMap()
    ): TitlesResponse

    @GET("od/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String,
    ): List<FavoriteArticle>

    @DELETE("od/favorites/{id}")
    suspend fun deleteFavoriteArticle(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ResponseBody

    @POST("od/favorites")
    suspend fun saveFavorite(
        @Header("Authorization") token: String,
        @Body favoriteArticle: FavoriteArticle
    ): FavoriteArticle

    @GET("od/markups")
    suspend fun getMarkups(
        @Header("Authorization") token: String,
    ): List<Markup>

    @DELETE("od/markups/{id}")
    suspend fun deleteMarkup(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ResponseBody

    @POST("od/markups")
    suspend fun saveMarkup(
        @Header("Authorization") token: String,
        @Body markup: Markup
    ): Markup

    @GET("od/readarticles")
    suspend fun getReadArticles(@Header("Authorization") token: String): List<ReadArticle>

    @POST("od/readarticles")
    suspend fun addReadArticle(
        @Header("Authorization") token: String,
        @Body readArticle: ReadArticle
    ): ResponseBody

    @PATCH("od/readarticles/{id}")
    suspend fun updateReadArticle(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body readArticle: ReadArticle
    ): ResponseBody

    @GET("bible/books")
    suspend fun getBibleBooks(): BibleBooks
}