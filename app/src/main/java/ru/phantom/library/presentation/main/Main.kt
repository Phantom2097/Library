package ru.phantom.library.presentation.main

import data.Position
import data.repository.LibraryRepository.addItemBook
import data.repository.LibraryRepository.addItemDisk
import data.repository.LibraryRepository.addItemNewspaper
import data.repository.LibraryRepository.getItemsCounter
import ru.phantom.library.domain.LibraryService
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.disk.Type.CD
import ru.phantom.library.data.entites.library.items.disk.Type.DVD
import ru.phantom.library.data.entites.library.items.newspaper.NewspaperImpl
import ru.phantom.library.data.entites.library.items.newspaper_with_month.Month.JANUARY
import ru.phantom.library.data.entites.library.items.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.library.presentation.start_console.showConsoleStartLibraryUI

fun main() {
    val libraryService = LibraryService()
    createBooks(libraryService)
    createNewspapers(libraryService)
    createDisks(libraryService)

    showConsoleStartLibraryUI()
}

private fun createBook(
    name: String,
    id: Int = getItemsCounter(),
    availability: Boolean = true,
    position: Position = if (availability) Position.LIBRARY else Position.UNKNOWN,
    author: String = " ",
    numberOfPages: Int? = null,
    service: LibraryService
) {
    addItemBook(
        BookImpl(
            LibraryItem(
                name = name,
                id = id,
                availability = availability,
                position = position
            ),
            service
        ).apply {
            this.author = author
            this.numberOfPages = numberOfPages
        })
}

fun createBooks(service: LibraryService) {
    createBook(
        name = "Котлин для профессионалов",
        author = "Джош Скин, Дэвид Грэнхол, Эндрю Бэйли",
        numberOfPages = 560,
        service = service
    )
    createBook(
        name = "Маугли",
        availability = false,
        author = "Джозеф Киплинг",
        numberOfPages = 202,
        service = service
    )
    createBook(
        name = "Kotlin Design Patterns and Best Practices",
        availability = false,
        position = Position.IN_READING_ROOM,
        author = "Alexey Soshin, Anton Arhipov",
        numberOfPages = 356,
        service = service
    )
    createBook(
        name = "Евгений Онегин",
        availability = false,
        position = Position.HOME,
        author = "Пушкин А.С.",
        numberOfPages = 320,
        service = service
    )
    createBook(
        name = "Алые Плинтуса",
        id = getItemsCounter(),
        availability = true,
        author = "Саша Зелёный",
        service = service
    )
    createBook(
        name = "Война и привет",
        id = getItemsCounter(),
        availability = true,
        service = service
    )
}

fun createNewspapers(service: LibraryService) {
    addItemNewspaper(
        NewspaperImpl(
            LibraryItem(
                name = "Русская правда",
                id = getItemsCounter(),
                availability = false,
                position = Position.IN_READING_ROOM
            ),
            service

        ).apply {
            issueNumber = 794
        })
    addItemNewspaper(
        NewspaperImpl(
            LibraryItem(
                name = "Русская правда",
                id = getItemsCounter(),
                availability = true,
            ),
            service

        ).apply {
            issueNumber = 795
        })
    addItemNewspaper(
        NewspaperImpl(
            LibraryItem(
                name = "Русская правда",
                id = getItemsCounter(),
                availability = true,
            ),
            service

        ).apply {
            issueNumber = 796
        })
    addItemNewspaper(
        NewspaperImpl(
            LibraryItem(
                name = "Русская ложь",
                id = getItemsCounter(),
                availability = true,
            ),
            service
        )
    )
    addItemNewspaper(
        NewspaperWithMonthImpl(
            LibraryItem(
                name = "Русская правда",
                id = getItemsCounter(),
                availability = true,
            ),
            service

        ).apply {
            issueNumber = 795
            issueMonth = JANUARY
        })
}

fun createDisks(service: LibraryService) {
    addItemDisk(
        DiskImpl(
            LibraryItem(
                name = "Дэдпул и Росомаха",
                id = getItemsCounter(),
                availability = true,
            ),
            service

        ).apply {
            type = DVD
        })

    addItemDisk(
        DiskImpl(
            LibraryItem(
                name = "Какая-то песня",
                id = getItemsCounter(),
                availability = true,
            ),
            service

        ).apply {
            type = CD
        })

    addItemDisk(
        DiskImpl(
            LibraryItem(
                name = "Просто диск",
                id = getItemsCounter(),
                availability = false,
                position = Position.HOME
            ),
            service

        )
    )

    addItemDisk(
        DiskImpl(
            LibraryItem(
                name = "1111",
                id = getItemsCounter(),
                availability = false,
                position = Position.HOME
            ),
            service
        )
    )
}