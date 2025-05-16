package ru.phantom.data.remote.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import ru.phantom.data.remote.model.GoogleBooksResponse
import ru.phantom.data.BuildConfig

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResult: Int = MAX_RESULTS,
        @Query("fields") fields: String =  FIELDS,
        @Query("key") key: String = BuildConfig.GOOGLE_BOOKS_API_KEY
    ): GoogleBooksResponse


    private companion object {
        private const val MAX_RESULTS = 20
        private const val FIELDS = "items(volumeInfo(title,authors,pageCount,industryIdentifiers(type,identifier)))"
    }
}