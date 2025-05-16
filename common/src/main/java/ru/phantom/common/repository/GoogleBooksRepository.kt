package ru.phantom.common.repository

import ru.phantom.common.entities.library.book.Book

interface GoogleBooksRepository {
    suspend fun getGoogleBooks(query: String): Result<List<Book?>>
}