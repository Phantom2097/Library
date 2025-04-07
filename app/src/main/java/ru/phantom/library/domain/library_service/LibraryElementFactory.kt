package ru.phantom.library.domain.library_service

import ru.phantom.library.data.Position
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.book.Book
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.Disk
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.disk.Type
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.data.entites.library.items.newspaper.newspaper_with_month.Month
import ru.phantom.library.data.entites.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.library.data.repository.LibraryRepository.getItemsCounter

object LibraryElementFactory {
    fun createBook(
        name: String,
        id: Int = getItemsCounter(),
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
                service,
                author = author.split(", "),
                numberOfPages = numberOfPages
            )
    }

    fun createNewspaper(
        name: String,
        id: Int = getItemsCounter(),
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
            service,
            issueNumber = issueNumber,
            issueMonth = issueMonth
        )
    }

    fun createDisk(
        name: String,
        id: Int = getItemsCounter(),
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
            service,
            type = type
        )
    }
}