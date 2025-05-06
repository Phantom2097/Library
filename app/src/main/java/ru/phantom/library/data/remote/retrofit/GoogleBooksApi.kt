package ru.phantom.library.data.remote.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import ru.phantom.library.data.remote.model.GoogleBooksResponse

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResult: Int = 20,
        @Query("fields") fields: String =  FIELDS,
        @Query("key") key: String = MY_API_KEY
    ): GoogleBooksResponse


    private companion object {
        private const val FIELDS = "items(volumeInfo(title,authors,pageCount,industryIdentifiers(type,identifier)))"
        // Тебе не стоит быть здесь
        private const val MY_API_KEY = "AIzaSyCYXGqEU4HpqyDiaedv-r_PtYXg9PwvWO0"
    }
}