package ru.phantom.library.data.repository

import kotlinx.coroutines.flow.Flow

interface ItemsRepository<T> {

    /**
     * Добавление нового элемента в репозиторий
     * @param item новый элемент
     */
    suspend fun addItems(item: T)

    /**
     * Удаление элемента из репозитория
     * @param position позиция элемента, который нужно удалить (соответствует позиции в RecyclerView)
     */
    suspend fun removeItem(position: Int)

    /**
     * Функция запрашивает все элементы из репозитория
     * @return Список элементов репозитория
     */
    suspend fun getItems() : Flow<List<T>>

    /**
     * Изменяет состояние существующего элемента
     * @param position позиция элемента, у которого нужно изменить состояние
     * @param newItem этот же элемент с изменённым состоянием
     */
    suspend fun changeItem(position: Int, newItem: T)
}