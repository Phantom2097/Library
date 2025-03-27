package ru.phantom.library.data.entites.library.items.newspaper_with_month

import ru.phantom.library.domain.LibraryService
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.newspaper.NewspaperImpl
import ru.phantom.library.data.entites.library.items.newspaper_with_month.Month.UNKNOWN
import presentation.colors.Colors.ANSI_BLUE
import presentation.colors.Colors.ANSI_RESET

class NewspaperWithMonthImpl(
    private val item: LibraryItem,
    libraryService: LibraryService
) : NewspaperImpl(item, libraryService),
    NewspaperWithMonth {

    override var issueMonth: Month = UNKNOWN

    override fun fullInformation(): String = this.toString()

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