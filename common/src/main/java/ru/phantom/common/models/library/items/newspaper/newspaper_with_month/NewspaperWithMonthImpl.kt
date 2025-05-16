package ru.phantom.common.models.library.items.newspaper.newspaper_with_month

import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.entities.library.newspaper.Newspaper
import ru.phantom.common.entities.library.newspaper.month.Month
import ru.phantom.common.entities.library.newspaper.month.Month.UNKNOWN
import ru.phantom.common.entities.library.newspaper.month.Monthly
import ru.phantom.common.library_service.LibraryService

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

