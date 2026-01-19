package ru.phantom.data.remote.retrofit

import jakarta.inject.Inject
import retrofit2.HttpException
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.repository.GoogleBooksRepository
import ru.phantom.data.mappers.GoogleBooksResponseMapper.toBooks
import java.io.IOException

internal class RemoteGoogleBooksRepository @Inject constructor(
    private val api: GoogleBooksApi
) : GoogleBooksRepository {
    override suspend fun getGoogleBooks(query: String): Result<List<Book?>> {
        return try {
            val response = api.getBooks(query)
            val books = response.toBooks()
            if (books.isEmpty()) {
                Result.failure(NetworkException("Ничего не нашлось", null))
            } else {
                Result.success(response.toBooks())
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Ошибка сети: проверьте подключение к интернету", e))
        } catch (e: HttpException) {
            val message = when (e.code()) {
                BAD_REQUEST -> "Сервер не может обработать запрос"
                FORBIDDEN -> "Доступ к ресурсу запрещён"
                NOT_FOUND -> "Такого ресурса нет"
                else -> "Ошибка сервера ${e.code()}"
            }
            Result.failure(NetworkException(message, e))
        } catch (e: Exception) {
            Result.failure(NetworkException("Неизвестная ошибка ${e.message}", e))
        }
    }
    private companion object {
        private const val BAD_REQUEST = 400
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
    }
}

private class NetworkException(message: String, cause: Throwable?) : Exception(message, cause)