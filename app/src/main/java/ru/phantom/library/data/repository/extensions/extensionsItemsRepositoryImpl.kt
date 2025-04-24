package ru.phantom.library.data.repository.extensions

import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.repository.ItemsRepository
import ru.phantom.library.data.repository.ItemsRepositoryImpl

/**
 * Функция расширение для использования эмулятора задержки и ошибок
 */
suspend fun ItemsRepository<BasicLibraryElement>.simulateRealRepository() {
    if (this is ItemsRepositoryImpl) {
        this.delayEmulator()
        this.errorEmulator()
    }
}