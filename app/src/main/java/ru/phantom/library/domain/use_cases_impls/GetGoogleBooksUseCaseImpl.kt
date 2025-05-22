package ru.phantom.library.domain.use_cases_impls

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.repository.GoogleBooksRepository
import ru.phantom.library.domain.use_cases.GetGoogleBooksUseCase
import javax.inject.Inject

class GetGoogleBooksUseCaseImpl @Inject constructor(
    override val repository: GoogleBooksRepository
) : GetGoogleBooksUseCase {
    override suspend operator fun invoke(query: String): Result<List<Book?>> {
        return withContext(Dispatchers.IO) {
            repository.getGoogleBooks(query)
        }
    }
}