package ru.phantom.library.domain.use_cases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.repository.GoogleBooksRepository
import javax.inject.Inject

class GetGoogleBooksUseCase @Inject constructor(
    private val repository: GoogleBooksRepository
) {
    suspend operator fun invoke(query: String): Result<List<Book?>> {
        return withContext(Dispatchers.IO) {
            repository.getGoogleBooks(query)
        }
    }
}