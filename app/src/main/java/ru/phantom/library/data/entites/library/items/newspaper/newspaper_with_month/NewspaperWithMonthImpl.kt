package ru.phantom.library.data.entites.library.items.newspaper.newspaper_with_month

import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.data.entites.library.items.newspaper.newspaper_with_month.Month.UNKNOWN
import ru.phantom.library.domain.library_service.LibraryService

data class NewspaperWithMonthImpl(
    override val item: LibraryItem,
    override val service: LibraryService,
    override var issueNumber: Int? = null,
    override var issueMonth: Month = UNKNOWN,
) :
    Newspaper(item, service),
    Monthly
{
    override fun toString(): String {
        val tempAvailability = service.showAvailability(item.availability)
        val tempIssueNumber = issueNumber ?: "*неизвестно*"
        val tempMonth = issueMonth.getMonth()

        val resultMessage = buildString {
            append("Газета ${item.name}\n")
            append("Выпуск: $tempIssueNumber\n")
            append("Месяц: $tempMonth\n")
            append("Доступна: $tempAvailability\n")
        }
        return resultMessage
    }
}

