package ru.phantom.library.data.entites.library.items.newspaper

import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.domain.library_service.LibraryService

data class NewspaperImpl(
    override val item: LibraryItem,
    override val libraryService: LibraryService
) :
    Newspaper(item, libraryService)
{
    override var issueNumber: Int? = null

    override fun toString(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        return "Выпуск: ${issueNumber ?: "*неизвестно*"} газеты ${item.name} с id: ${item.id} доступен: $tempAvailability\n"
    }
}