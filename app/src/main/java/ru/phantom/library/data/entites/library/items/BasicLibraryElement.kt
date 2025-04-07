package ru.phantom.library.data.entites.library.items

import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import ru.phantom.library.data.Position
import ru.phantom.library.data.entites.library.Readable
import ru.phantom.library.data.entites.library.Showable
import ru.phantom.library.domain.library_service.LibraryService

abstract class BasicLibraryElement(
    open val item: LibraryItem,
    open val service: LibraryService,
) :
    Readable,
    Showable
{

    // Вывод информации
    override fun briefInformation(): String {
        val tempAvailability = service.showAvailability(item.availability)
        return "Предмет \"${item.name}\" доступен: $tempAvailability"
    }

    override fun fullInformation(): String = this.toString()

    // Возврат в библиотеку
    override fun returnInLibrary(): String =
        if (service.setAvailability(true, item)) {

            service.setPosition(Position.LIBRARY, item)

            "$ANSI_GREEN*Предмет ${item.name} id: ${item.id} возвращен в библиотеку*$ANSI_RESET\n" +
                    "Предмет \"${item.name}\" возвращен, спасибо\n"
        } else {
            "Предмет \"${item.name}\" не нужно возвращать, он всё ещё в библиотеке\n"
        }

    // Читать в читальном зале
    override fun readInTheReadingRoom(): String =
        if (service.setAvailability(false, item)) {

            service.setPosition(Position.IN_READING_ROOM, item)

            "$ANSI_GREEN*Предмет ${item.name} id: ${item.id} взяли в читальный зал*$ANSI_RESET\n" +
                    "Предмет \"${item.name}\" ваша, не забудьте вернуть до закрытия\n"
        } else {
            "Извините предмет \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.IN_READING_ROOM -> "$ANSI_GREEN*Предмет забрали в читальный зал*$ANSI_RESET\n"
                        Position.HOME -> "$ANSI_GREEN*Предмет забрали домой*$ANSI_RESET\n"
                        else -> "$ANSI_GREEN*Кажется мы его потеряли...*$ANSI_RESET\n"
                    }
        }

    // Взять домой
    override fun takeToHome(): String =
        if (service.setAvailability(false, item)) {

            service.setPosition(Position.HOME, item)

            "$ANSI_GREEN*Предмет ${item.name} id: ${item.id} взяли домой*$ANSI_RESET\n" +
                    "Предмет \"${item.name}\" ваша, не забудьте вернуть в течение 30 дней\n"
        } else {
            "Извините предмет \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.IN_READING_ROOM -> "$ANSI_GREEN*Предмет забрали в читальный зал*$ANSI_RESET\n"
                        Position.HOME -> "$ANSI_GREEN*Предмет забрали домой*$ANSI_RESET\n"
                        else -> "$ANSI_GREEN*Кажется мы его потеряли...*$ANSI_RESET\n"
                    }
        }

    override fun toString(): String {
        val tempAvailability = service.showAvailability(item.availability)
        return "Предмет: ${item.name} с id: ${item.id} доступен: $tempAvailability\n"
    }
}