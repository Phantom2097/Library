package ru.phantom.common.entities.library.newspaper

import ru.phantom.common.colors.Colors.ANSI_GREEN
import ru.phantom.common.colors.Colors.ANSI_RESET
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.entities.library.Position
import ru.phantom.common.library_service.LibraryService
import ru.phantom.common.models.library.items.LibraryItem

abstract class Newspaper(
    override val item: LibraryItem,
    override val service: LibraryService,
) :
    BasicLibraryElement(item, service)
{
    abstract var issueNumber: Int?

    // Вывод информации
    override fun briefInformation(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        return "Газета \"${item.name} выпуск ${issueNumber ?: "*неизвестно*"}\" доступна: $tempAvailability" // Без выпуска не особо понятно, какая это газета
    }

    override fun fullInformation(): String = this.toString()

    // Возврат в библиотеку
    override fun returnInLibrary(): String =
        if (service.setAvailability(true, item)) {

            service.setPosition(Position.LIBRARY, item)

            "$ANSI_GREEN*Газета ${item.name} выпуск ${issueNumber ?: "*неизвестно*"} id: ${item.id} вернули в библиотеку*$ANSI_RESET\n" +
                    "Газета \"${item.name}\" возвращена\n"
        } else {
            "Газету \"${item.name}\" не нужно возвращать, она всё ещё в библиотеке\n"
        }

    // Читать в читальном зале
    override fun readInTheReadingRoom(): String =
        if (service.setAvailability(false, item)) {

            service.setPosition(Position.IN_READING_ROOM, item)

            "$ANSI_GREEN*Газета ${item.name} выпуск ${issueNumber ?: "*неизвестно*"} id: ${item.id} взяли в читальный зал*$ANSI_RESET\n" +
                    "Газета \"${item.name}\" ваша, не забудьте вернуть до закрытия\n"
        } else {
            "Извините газету \"${item.name}\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.IN_READING_ROOM -> "$ANSI_GREEN*Газету взяли в читальный зал*$ANSI_RESET\n"
                        else -> "$ANSI_GREEN*Кажется мы её потеряли...*$ANSI_RESET\n"
                    }
        }
}