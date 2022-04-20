package com.ovidium.comoriod.api

import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
import com.ovidium.comoriod.data.books.BooksResponse
import com.ovidium.comoriod.data.recentlyaddedbooks.RecentlyAddedBooksResponse
import com.ovidium.comoriod.data.recommended.RecommendedResponse
import com.ovidium.comoriod.data.search.SearchResponse
import com.ovidium.comoriod.data.trending.TrendingResponse
import com.ovidium.comoriod.data.volumes.VolumesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("authors")
    suspend fun getAuthors(): AuthorsResponse

    @GET("volumes")
    suspend fun getVolumes(): VolumesResponse

    @GET("books")
    suspend fun getBooks(): BooksResponse

    @GET("recentlyaddedbooks")
    suspend fun getRecentlyAddedBooks(): RecentlyAddedBooksResponse

    @GET("recommended")
    suspend fun getRecommended(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 5
    ): RecommendedResponse

    @GET("trendingarticles")
    suspend fun getTrending(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 5
    ): TrendingResponse

    @GET("titles/completion")
    suspend fun autocomplete(@Query("prefix") prefix: String): AutocompleteResponse

    @GET("articles")
    suspend fun search(
        @Query("q") q: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): SearchResponse

    @GET("data/{id}.json")
    suspend fun getArticle(
        @Path("id") id: String
    ): ArticleResponse
}