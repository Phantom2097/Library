package ru.phantom.common.repository.extensions

/**
 * Функция расширение для использования эмулятора задержки и ошибок
 */
interface SimulateRealRepository {
    suspend fun simulateRealRepository()
    suspend fun delayLikeRealRepository()
}