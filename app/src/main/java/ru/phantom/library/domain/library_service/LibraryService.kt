package ru.phantom.library.domain.library_service

import ru.phantom.library.data.Position
import ru.phantom.library.data.entites.library.items.LibraryItem

object LibraryService: Service {

//    fun getName(item: LibraryItem): String = item.name
//    fun getId(item: LibraryItem): Int = item.id

    // Доступность в текстовом формате
    fun showAvailability(availability: Boolean) = if (availability) "Да" else "Нет"
    // Изменение доступности
    fun setAvailability(newAvailability: Boolean, item: LibraryItem): Boolean {
        return newAvailability != item.availability.also {
            item.availability = newAvailability
        }
    }
    // Изменение позиции
    fun setPosition(position: Position, item: LibraryItem) {
        item.position = position
    }
}