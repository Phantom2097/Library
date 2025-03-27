package ru.phantom.library.data.entites.library.items.newspaper

import ru.phantom.library.domain.LibraryService
import data.Position
import ru.phantom.library.data.entites.library.Readable
import ru.phantom.library.data.entites.library.Showable
import ru.phantom.library.data.entites.library.items.LibraryItem
import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import ru.phantom.library.data.entites.library.items.Itemable

open class NewspaperImpl(
    private val item: LibraryItem,
    private val libraryService: LibraryService
) :
    Itemable,
    Newspaper,
    Readable,
    Showable {

    override var issueNumber: Int? = null

    override fun getName(): String = item.name
    override fun getId(): Int = item.id
    override fun getAvailability(): Boolean = item.availability

    override fun setAvailability() {
        libraryService.setAvailability(!getAvailability(), item)
    }

    // Вывод информации
    override fun briefInformation(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        return "Газета \"${item.name} выпуск ${issueNumber ?: "*неизвестно*"}\" доступна: $tempAvailability" // Без выпуска не особо понятно, какая это газета
    }

    override fun fullInformation(): String = this.toString()

    // Возврат в библиотеку
    override fun returnInLibrary(): String =
        if (libraryService.setAvailability(true, item)) {

            libraryService.setPosition(Position.LIBRARY, item)

            "$ANSI_GREEN*Газета ${item.name} выпуск ${issueNumber ?: "*неизвестно*"} id: ${item.id} вернули в библиотеку*$ANSI_RESET\n" +
                    "Газета \"${item.name}\" возвращена\n"
        } else {
            "Газету \"${item.name}\" не нужно возвращать, она всё ещё в библиотеке\n"
        }

    // Читать в читальном зале
    override fun readInTheReadingRoom(): String =
        if (libraryService.setAvailability(false, item)) {

            libraryService.setPosition(Position.IN_READING_ROOM, item)

            "$ANSI_GREEN*Газета ${item.name} выпуск ${issueNumber ?: "*неизвестно*"} id: ${item.id} взяли в читальный зал*$ANSI_RESET\n" +
                    "Газета \"${item.name}\" ваша, не забудьте вернуть до закрытия\n"
        } else {
            "Извините газету \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.IN_READING_ROOM -> "$ANSI_GREEN*Газету взяли в читальный зал*$ANSI_RESET\n"
                        else -> "$ANSI_GREEN*Кажется мы её потеряли...*$ANSI_RESET\n"
                    }
        }

    override fun toString(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        return "Выпуск: ${issueNumber ?: "*неизвестно*"} газеты ${item.name} с id: ${item.id} доступен: $tempAvailability\n"
    }
}