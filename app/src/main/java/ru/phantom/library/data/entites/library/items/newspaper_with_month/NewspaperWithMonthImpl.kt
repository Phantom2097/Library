package ru.phantom.library.data.entites.library.items.newspaper_with_month

import presentation.colors.Colors.ANSI_BLUE
import presentation.colors.Colors.ANSI_RESET
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.newspaper_with_month.Month.UNKNOWN
import ru.phantom.library.domain.library_service.LibraryService

data class NewspaperWithMonthImpl (
    override val item: LibraryItem,
    override val libraryService: LibraryService
) : NewspaperWithMonth(item, libraryService)
{

    override var issueMonth: Month = UNKNOWN
    override var issueNumber: Int? = null

//    override fun fullInformation(): String = this.toString()

    override fun toString(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        val tempMonth = issueMonth.getMonth()
        val tempIssueNumber = issueNumber ?: "*неизвестно*"

        val resultMessage = buildString {
            append("Выпуск: $tempIssueNumber газеты ${item.name}")
            append("$ANSI_BLUE за $tempMonth$ANSI_RESET")
            append(" с id: ${item.id} доступен: $tempAvailability\n")
        }

        return resultMessage
    }
}