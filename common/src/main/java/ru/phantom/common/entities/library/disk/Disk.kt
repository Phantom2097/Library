package ru.phantom.common.entities.library.disk

import ru.phantom.common.colors.Colors.ANSI_GREEN
import ru.phantom.common.colors.Colors.ANSI_RESET
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.entities.library.Position
import ru.phantom.common.library_service.LibraryService
import ru.phantom.common.models.library.items.LibraryItem

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