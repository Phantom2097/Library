package ru.phantom.common.library_service

import ru.phantom.common.entities.library.Position
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.entities.library.disk.Disk
import ru.phantom.common.entities.library.disk.Type
import ru.phantom.common.entities.library.newspaper.Newspaper
import ru.phantom.common.entities.library.newspaper.month.Month
import ru.phantom.common.library_service.LibraryRepository.getItemsCounter
import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.models.library.items.book.BookImpl
import ru.phantom.common.models.library.items.disk.DiskImpl
import ru.phantom.common.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl

object LibraryElementFactory {
    fun createBook (
        name: String,
        id: Long = getItemsCounter(),
        availability: Boolean = true,
        position: Position = if (availability) Position.LIBRARY else Position.UNKNOWN,
        author: String = "",
        numberOfPages: Int? = null,
        service: LibraryService = LibraryService
    ): Book {
        return BookImpl(
            LibraryItem(
                name = name,
                id = id,
                availability = availability,
                position = position
            ),
            service = service,
            author = author.trim().split(", "),
            numberOfPages = numberOfPages
        )
    }

    // Перегрузка со сразу готовым item
    fun createBook (
        item: LibraryItem,
        author: String = "",
        numberOfPages: Int? = null,
        service: LibraryService = LibraryService
    ): Book {
        return BookImpl(
            item = item,
            service = service,
            author = author.split(", "),
            numberOfPages = numberOfPages
        )
    }

    fun createNewspaper(
        name: String,
        id: Long = getItemsCounter(),
        availability: Boolean = true,
        position: Position = if (availability) Position.LIBRARY else Position.UNKNOWN,
        issueNumber: Int? = null,
        issueMonth: Month = Month.UNKNOWN,
        service: LibraryService = LibraryService
    ): Newspaper {
        return NewspaperWithMonthImpl(
            LibraryItem(
                name = name,
                id = id,
                availability = availability,
                position = position
            ),
            service = service,
            issueNumber = issueNumber,
            issueMonth = issueMonth
        )
    }

    fun createNewspaper (
        item: LibraryItem,
        issueNumber: Int? = null,
        issueMonth: Month = Month.UNKNOWN,
        service: LibraryService = LibraryService
    ): Newspaper {
        return NewspaperWithMonthImpl(
            item = item,
            service = service,
            issueNumber = issueNumber,
            issueMonth = issueMonth
        )
    }

    fun createDisk(
        name: String,
        id: Long = getItemsCounter(),
        availability: Boolean = true,
        position: Position = if (availability) Position.LIBRARY else Position.UNKNOWN,
        type: Type = Type.UNKNOWN,
        service: LibraryService = LibraryService
    ): Disk {
        return DiskImpl(
            LibraryItem(
                name = name,
                id = id,
                availability = availability,
                position = position
            ),
            service = service,
            type = type
        )
    }

    fun createDisk (
        item: LibraryItem,
        type: Type = Type.UNKNOWN,
        service: LibraryService = LibraryService
    ): Disk {
        return DiskImpl(
            item = item,
            service = service,
            type = type
        )
    }
}