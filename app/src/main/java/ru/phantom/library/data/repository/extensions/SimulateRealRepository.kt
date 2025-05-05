package ru.phantom.library.data.repository.extensions

/**
 * Функция расширение для использования эмулятора задержки и ошибок
 */
fun interface SimulateRealRepository {
    suspend fun simulateRealRepository()
}