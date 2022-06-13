package com.ovidium.comoriod.api

import com.ovidium.comoriod.data.article.ArticleResponse
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.autocomplete.AutocompleteResponse
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
    @GET("types")
    suspend fun getTypes(
        @Query("q") q: String = "",
        @QueryMap params: Map<String, String> = emptyMap()
    ): TypesResponse

    @GET("authors")
    suspend fun getAuthors(
        @Query("q") q: String = "",
        @QueryMap params: Map<String, String> = emptyMap()
    ): AuthorsResponse

    @GET("volumes")
    suspend fun getVolumes(
        @Query("q") q: String = "",
        @QueryMap params: Map<String, String> = emptyMap()
    ): VolumesResponse

    @GET("books")
    suspend fun getBooks(
        @Query("q") q: String = "",
        @QueryMap params: Map<String, String> = emptyMap()
    ): BooksResponse

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
        @Query("offset") offset: Int = 0,
        @QueryMap params: Map<String, String> = emptyMap()
    ): SearchResponse

    @GET("data/{id}.json")
    suspend fun getArticle(
        @Path("id") id: String
    ): ArticleResponse

    @GET("titles")
    suspend fun getTitles(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @QueryMap params: Map<String, String> = emptyMap()
    ): TitlesResponse

    @GET("favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String,
    ): List<FavoriteArticle>

    @DELETE("favorites/{id}")
    suspend fun deleteFavoriteArticle(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ResponseBody

    @POST("favorites")
    suspend fun saveFavorite(
        @Header("Authorization") token: String,
        @Body favoriteArticle: FavoriteArticle
    ): FavoriteArticle

    @GET("markups")
    suspend fun getMarkups(
        @Header("Authorization") token: String,
    ): List<Markup>

    @DELETE("markups/{id}")
    suspend fun deleteMarkup(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ResponseBody

    @POST("markups")
    suspend fun saveMarkup(
        @Header("Authorization") token: String,
        @Body markup: Markup
    ): Markup

}