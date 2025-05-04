package ru.phantom.library.data.repository

interface ItemsRepository<T> {

    /**
     * Добавление нового элемента в репозиторий
     * @param item новый элемент
     */
    suspend fun addItems(item: T)

    /**
     * Удаление элемента из репозитория
     * @param id id элемента, который нужно удалить
     */
    suspend fun removeItem(id: Long)

    /**
     * Функция запрашивает все элементы из репозитория
     * @return Список элементов репозитория
     */
    suspend fun getItems(limit: Int, offset: Int) : List<T>

    /**
     * Изменяет состояние существующего элемента
     * @param position позиция элемента, у которого нужно изменить состояние
     * @param newItem этот же элемент с изменённым состоянием
     */
    suspend fun changeItem(position: Int, newItem: T)

    suspend fun getTotalCount(): Long
}