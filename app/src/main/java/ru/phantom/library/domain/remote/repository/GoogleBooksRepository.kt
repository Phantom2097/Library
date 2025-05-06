package ru.phantom.library.domain.remote.repository

import ru.phantom.library.domain.entities.library.book.Book

interface GoogleBooksRepository {
    suspend fun getGoogleBooks(query: String): List<Book?>
}