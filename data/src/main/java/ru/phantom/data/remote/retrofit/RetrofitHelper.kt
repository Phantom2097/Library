package ru.phantom.data.remote.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Converter
import retrofit2.Retrofit

object RetrofitHelper {

    private fun createConverterFactory(): Converter.Factory {
        val contentType = MediaType.get("application/json")
        val json = Json {
            ignoreUnknownKeys = true
        }

        return json.asConverterFactory(contentType)
    }

    fun createRetrofit() : GoogleBooksApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(createConverterFactory())
            .build()

        return retrofit.create(GoogleBooksApi::class.java)
    }
}