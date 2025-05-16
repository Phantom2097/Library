package ru.phantom.common.models.library.items.disk

import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.entities.library.disk.Digitable
import ru.phantom.common.entities.library.disk.Disk
import ru.phantom.common.entities.library.disk.Type
import ru.phantom.common.library_service.LibraryService

data class DiskImpl(
    override val item: LibraryItem,
    override val service: LibraryService,
    override var type: Digitable = Type.UNKNOWN
) : Disk(item, service) {

    override fun toString(): String {
        val tempAvailability = service.showAvailability(item.availability)
        return buildString {
            append("Диск: ${item.name}\n")
            append("Тип: ${type.getType()}\n")
            append("Доступен: $tempAvailability\n")
        }
    }
}
