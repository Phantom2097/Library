package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.repository.GoogleBooksRepository

interface GetGoogleBooksUseCase {
    val repository: GoogleBooksRepository

    suspend operator fun invoke(query: String): Result<List<Book?>>
}