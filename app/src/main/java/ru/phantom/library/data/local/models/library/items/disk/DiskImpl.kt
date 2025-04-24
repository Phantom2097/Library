package ru.phantom.library.data.local.models.library.items.disk

import ru.phantom.library.data.local.models.library.items.LibraryItem
import ru.phantom.library.domain.entities.library.disk.Digitable
import ru.phantom.library.domain.entities.library.disk.Disk
import ru.phantom.library.domain.entities.library.disk.Type
import ru.phantom.library.domain.library_service.LibraryService

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
