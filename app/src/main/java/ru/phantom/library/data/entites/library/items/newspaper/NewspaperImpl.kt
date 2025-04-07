package ru.phantom.library.data.entites.library.items.newspaper

import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.domain.library_service.LibraryService

data class NewspaperImpl(
    override val item: LibraryItem,
    override val service: LibraryService,
    override var issueNumber: Int? = null
) :
    Newspaper(item, service) {
    override fun toString(): String {
        val tempAvailability = service.showAvailability(item.availability)
        val tempIssueNumber = issueNumber ?: "*неизвестно*"

        val resultMessage = buildString {
            append("Газета ${item.name}\n")
            append("Выпуск: $tempIssueNumber\n")
            append("Доступна: $tempAvailability\n")
        }
        return resultMessage
    }
}
