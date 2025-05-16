package ru.phantom.common.models.library.items.newspaper

import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.entities.library.newspaper.Newspaper
import ru.phantom.common.library_service.LibraryService

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
