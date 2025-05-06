package ru.phantom.library.data.remote.retrofit

import ru.phantom.library.data.remote.model.GoogleBooksResponseMapper.toBooks
import ru.phantom.library.domain.entities.library.book.Book
import ru.phantom.library.domain.remote.repository.GoogleBooksRepository

class RemoteGoogleBooksRepository(
    private val api: GoogleBooksApi
) : GoogleBooksRepository {
    override suspend fun getGoogleBooks(query: String): List<Book?> {
        val response = api.getBooks(query)
        return response.toBooks()
    }
}