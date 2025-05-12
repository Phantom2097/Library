package ru.phantom.common.entities.library.book

import presentation.colors.Colors
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.entities.library.Position
import ru.phantom.common.library_service.LibraryService
import ru.phantom.common.models.library.items.LibraryItem

abstract class Book (
    override val item: LibraryItem,
    override val service: LibraryService,
) :
    BasicLibraryElement(item, service)
{
    abstract var author: List<String>
    abstract var numberOfPages: Int?

    // Вывод информации
    override fun briefInformation(): String {
        val tempAvailability = service.showAvailability(item.availability)
        return "Книга \"${item.name}\" доступна: $tempAvailability"
    }

    override fun fullInformation(): String = this.toString()

    // Возврат в библиотеку
    override fun returnInLibrary(): String =
        if (service.setAvailability(true, item)) {

            service.setPosition(Position.LIBRARY, item)

            "${Colors.ANSI_GREEN}*Книга ${item.name} id: ${item.id} возвращена в библиотеку*${Colors.ANSI_RESET}\n" +
                    "Книга \"${item.name}\" возвращена, спасибо\n"
        } else {
            "Книгу \"${item.name}\" не нужно возвращать, она всё ещё в библиотеке\n"
        }

    // Читать в читальном зале
    override fun readInTheReadingRoom(): String =
        if (service.setAvailability(false, item)) {

            service.setPosition(Position.IN_READING_ROOM, item)

            "${Colors.ANSI_GREEN}*Книга ${item.name} id: ${item.id} взяли в читальный зал*${Colors.ANSI_RESET}\n" +
                    "Книга \"${item.name}\" ваша, не забудьте вернуть до закрытия\n"
        } else {
            "Извините книгу \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.IN_READING_ROOM -> "${Colors.ANSI_GREEN}*Книгу забрали в читальный зал*${Colors.ANSI_RESET}\n"
                        Position.HOME -> "${Colors.ANSI_GREEN}*Книгу забрали домой*${Colors.ANSI_RESET}\n"
                        else -> "${Colors.ANSI_GREEN}*Кажется мы её потеряли...*${Colors.ANSI_RESET}\n"
                    }
        }

    // Взять домой
    override fun takeToHome(): String =
        if (service.setAvailability(false, item)) {

            service.setPosition(Position.HOME, item)

            "${Colors.ANSI_GREEN}*Книга ${item.name} id: ${item.id} взяли домой*${Colors.ANSI_RESET}\n" +
                    "Книга \"${item.name}\" ваша, не забудьте вернуть в течение 30 дней\n"
        } else {
            "Извините книгу \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.IN_READING_ROOM -> "${Colors.ANSI_GREEN}*Книгу забрали в читальный зал*${Colors.ANSI_RESET}\n"
                        Position.HOME -> "${Colors.ANSI_GREEN}*Книгу забрали домой*${Colors.ANSI_RESET}\n"
                        else -> "${Colors.ANSI_GREEN}*Кажется мы её потеряли...*${Colors.ANSI_RESET}\n"
                    }
        }
}