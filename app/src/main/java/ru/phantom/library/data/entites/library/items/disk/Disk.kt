package ru.phantom.library.data.entites.library.items.disk

import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import ru.phantom.library.data.Position
import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.domain.library_service.LibraryService

abstract class Disk(
    override val item: LibraryItem,
    override val service: LibraryService
) :
    BasicLibraryElement(item, service)
{
    abstract var type: Digitable

    // Вывод информации
    override fun briefInformation(): String {
        val tempAvailability = service.showAvailability(item.availability)
        return "Диск \"${item.name}\" доступен: $tempAvailability"
    }

    override fun fullInformation(): String = this.toString()

    // Возврат в библиотеку
    override fun returnInLibrary(): String =
        if (service.setAvailability(true, item)) {

            service.setPosition(Position.LIBRARY, item)

            "$ANSI_GREEN*Диск ${item.name} id: ${item.id} вернули в библиотеку*$ANSI_RESET\n" +
                    "Диск \"${item.name}\" возвращен, спасибо\n"
        } else {
            "Диск \"${item.name}\" не нужно возвращать, он всё ещё в библиотеке\n"
        }

    // Взять домой
    override fun takeToHome(): String =
        if (service.setAvailability(false, item)) {

            service.setPosition(Position.HOME, item)

            "$ANSI_GREEN*Диск ${item.name}} id: ${item.id} взяли домой*$ANSI_RESET\n" +
                    "Диск \"${item.name}\" ваш, не забудьте вернуть в течение 30 дней\n"
        } else {
            "Извините диск \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.HOME -> "$ANSI_GREEN*Диск забрали домой*$ANSI_RESET\n"
                        else -> "$ANSI_GREEN*Кажется мы его потеряли...*$ANSI_RESET\n"
                    }
        }
}