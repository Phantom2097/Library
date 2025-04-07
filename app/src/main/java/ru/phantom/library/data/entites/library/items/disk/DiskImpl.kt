package ru.phantom.library.data.entites.library.items.disk

import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.disk.Type.UNKNOWN
import ru.phantom.library.domain.library_service.LibraryService

data class DiskImpl(
    override val item: LibraryItem,
    override val service: LibraryService,
    override var type: Digitable = UNKNOWN
) : Disk(item, service) {

    override fun toString(): String {
        val tempAvailability = if (item.availability) "Да" else "Нет"
        return "${type.getType()} ${item.name} доступен: $tempAvailability\n"
    }
}
