package ru.phantom.library.data.entites.library.items.disk

import ru.phantom.library.domain.library_service.LibraryService
import ru.phantom.library.data.Position
import ru.phantom.library.data.entites.library.Showable
import ru.phantom.library.data.entites.library.Readable
import ru.phantom.library.data.entites.library.items.Digitable
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.disk.Type.UNKNOWN
import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import ru.phantom.library.data.entites.library.items.Itemable

data class DiskImpl(
    val item: LibraryItem,
    private val libraryService: LibraryService
) :
    Itemable,
    Disk,
    Readable,
    Showable {

    override var type: Digitable = UNKNOWN

    private val name = item.name

//    override fun getItem(): LibraryItem = item
    override fun getName(): String = item.name
    override fun getId(): Int = item.id
    override fun getAvailability(): Boolean = item.availability

    override fun setAvailability() {
        libraryService.setAvailability(!getAvailability(), item)
    }

    // Вывод информации
    override fun briefInformation(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        return "Диск \"$name\" доступен: $tempAvailability"
    }

    override fun fullInformation(): String = this.toString()

    // Возврат в библиотеку
    override fun returnInLibrary(): String =
        if (libraryService.setAvailability(true, item)) {

            libraryService.setPosition(Position.LIBRARY, item)

            "$ANSI_GREEN*Диск $name id: ${item.id} вернули в библиотеку*$ANSI_RESET\n" +
                    "Диск \"$name\" возвращен, спасибо\n"
        } else {
            "Диск \"$name\" не нужно возвращать, он всё ещё в библиотеке\n"
        }

    // Взять домой
    override fun takeToHome(): String =
        if (libraryService.setAvailability(false, item)) {

            libraryService.setPosition(Position.HOME, item)

            "$ANSI_GREEN*Диск $name} id: ${item.id} взяли домой*$ANSI_RESET\n" +
                    "Диск \"$name\" ваш, не забудьте вернуть в течение 30 дней\n"
        } else {
            "Извините диск \"$name\" нельзя получить, можете посмотреть другие\n" +
                    when (item.position) {
                        Position.HOME -> "$ANSI_GREEN*Диск забрали домой*$ANSI_RESET\n"
                        else -> "$ANSI_GREEN*Кажется мы его потеряли...*$ANSI_RESET\n"
                    }
        }

    override fun toString(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        return "${type.getType()} $name доступен: $tempAvailability\n"
    }
}