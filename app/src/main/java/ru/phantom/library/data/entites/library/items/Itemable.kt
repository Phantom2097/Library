package ru.phantom.library.data.entites.library.items

// это тоже нужно в PR
// Добавил этот интерфейс для получения данных, чтобы отображать информацию в UI
interface Itemable {
    fun getItem(): LibraryItem
    fun getName(): String
    fun getId(): Int

    fun getAvailability(): Boolean
    fun setAvailability()
}