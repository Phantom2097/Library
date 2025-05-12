package ru.phantom.data.remote.retrofit

import retrofit2.HttpException
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.repository.GoogleBooksRepository
import ru.phantom.data.remote.model.GoogleBooksResponseMapper.toBooks
import java.io.IOException

class RemoteGoogleBooksRepository(
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
                400 -> "Сервер не может обработать запрос"
                403 -> "Доступ к ресурсу запрещён"
                404 -> "Такого ресурса нет"
                else -> "Ошибка сервера ${e.code()}"
            }
            Result.failure(NetworkException(message, e))
        } catch (e: Exception) {
            Result.failure(NetworkException("Неизвестная ошибка ${e.message}", e))
        }
    }
}

class NetworkException(message: String, cause: Throwable?) : Exception(message, cause)