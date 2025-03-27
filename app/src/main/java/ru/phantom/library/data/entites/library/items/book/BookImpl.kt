package ru.phantom.library.data.entites.library.items.book

import ru.phantom.library.domain.LibraryService
import data.Position
import ru.phantom.library.data.entites.library.Readable
import ru.phantom.library.data.entites.library.Showable
import ru.phantom.library.data.entites.library.items.LibraryItem
import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import ru.phantom.library.data.entites.library.items.Itemable

class BookImpl(
    private val item: LibraryItem,
    private val libraryService: LibraryService
) :
    Itemable,
    Book,
    Readable,
    Showable {

    override var author: String = " "
    override var numberOfPages: Int? = null

    override fun getName(): String = item.name
    override fun getId(): Int = item.id
    override fun getAvailability(): Boolean = item.availability

    override fun setAvailability() {
        libraryService.setAvailability(!this.getAvailability(), item)
    }

    // Вывод информации
    override fun briefInformation(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        return "Книга \"${item.name}\" доступна: $tempAvailability"
    }

    override fun fullInformation(): String = this.toString()

    // Возврат в библиотеку
    override fun returnInLibrary(): String =
        if (libraryService.setAvailability(true, item)) {

            libraryService.setPosition(Position.LIBRARY, item)

            "$ANSI_GREEN*Книга ${item.name} id: ${item.id} возвращена в библиотеку*$ANSI_RESET\n" +
                    "Книга \"${item.name}\" возвращена, спасибо\n"
        } else {
            "Книгу \"${item.name}\" не нужно возвращать, она всё ещё в библиотеке\n"
        }

    // Читать в читальном зале
    override fun readInTheReadingRoom(): String =
        if (libraryService.setAvailability(false, item)) {

            libraryService.setPosition(Position.IN_READING_ROOM, item)

            "$ANSI_GREEN*Книга ${item.name} id: ${item.id} взяли в читальный зал*$ANSI_RESET\n" +
                    "Книга \"${item.name}\" ваша, не забудьте вернуть до закрытия\n"
        } else {
            "Извините книгу \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.IN_READING_ROOM -> "$ANSI_GREEN*Книгу забрали в читальный зал*$ANSI_RESET\n"
                        Position.HOME -> "$ANSI_GREEN*Книгу забрали домой*$ANSI_RESET\n"
                        else -> "$ANSI_GREEN*Кажется мы её потеряли...*$ANSI_RESET\n"
                    }
        }

    // Взять домой
    override fun takeToHome(): String =
        if (libraryService.setAvailability(false, item)) {

            libraryService.setPosition(Position.HOME, item)

            "$ANSI_GREEN*Книга ${item.name} id: ${item.id} взяли домой*$ANSI_RESET\n" +
                    "Книга \"${item.name}\" ваша, не забудьте вернуть в течение 30 дней\n"
        } else {
            "Извините книгу \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.IN_READING_ROOM -> "$ANSI_GREEN*Книгу забрали в читальный зал*$ANSI_RESET\n"
                        Position.HOME -> "$ANSI_GREEN*Книгу забрали домой*$ANSI_RESET\n"
                        else -> "$ANSI_GREEN*Кажется мы её потеряли...*$ANSI_RESET\n"
                    }
        }

    override fun toString(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        val tempNumberOfPages = if ((numberOfPages ?: -1) != -1) numberOfPages.toString() else "*неизвестно*"
        val tempAuthor = author.ifBlank { "*неизвестно*" }
        return "Книга: ${item.name} ($tempNumberOfPages стр.) автор: $tempAuthor с id: ${item.id} доступна: $tempAvailability\n"
    }
}