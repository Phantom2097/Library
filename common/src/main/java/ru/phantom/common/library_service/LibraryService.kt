package ru.phantom.common.library_service

import ru.phantom.common.entities.library.Position
import ru.phantom.common.models.library.items.LibraryItem

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