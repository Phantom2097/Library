package ru.phantom.library.presentation_console.main

import ru.phantom.common.entities.library.Position
import ru.phantom.common.entities.library.disk.Type.CD
import ru.phantom.common.entities.library.disk.Type.DVD
import ru.phantom.common.entities.library.newspaper.month.Month.JANUARY
import ru.phantom.common.library_service.LibraryElementFactory.createBook
import ru.phantom.common.library_service.LibraryElementFactory.createDisk
import ru.phantom.common.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.common.library_service.LibraryRepository.addItemBook
import ru.phantom.common.library_service.LibraryRepository.addItemDisk
import ru.phantom.common.library_service.LibraryRepository.addItemNewspaper
import ru.phantom.common.library_service.LibraryRepository.getItemsCounter
import ru.phantom.common.library_service.LibraryService
import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.models.library.items.disk.DiskImpl
import ru.phantom.common.models.library.items.newspaper.NewspaperImpl
import ru.phantom.common.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.library.presentation_console.start_console.showConsoleStartLibraryUI

fun main() {
    val libraryService = LibraryService
    createBooks(libraryService)
    createNewspapers(libraryService)
    createDisks(libraryService)

    showConsoleStartLibraryUI()
}


private fun createBooks(service: LibraryService) {
    addItemBook(
        createBook(
            name = "Котлин для профессионалов",
            author = "Джош Скин, Дэвид Грэнхол, Эндрю Бэйли",
            numberOfPages = 560,
            service = service
        )
    )

    addItemBook(
        createBook(
            name = "Маугли",
            availability = false,
            author = "Джозеф Киплинг",
            numberOfPages = 202,
            service = service
        )
    )
    addItemBook(
        createBook(
            name = "Kotlin Design Patterns and Best Practices",
            availability = false,
            position = Position.IN_READING_ROOM,
            author = "Alexey Soshin, Anton Arhipov",
            numberOfPages = 356,
            service = service
        )
    )
    addItemBook(
        createBook(
            name = "Евгений Онегин",
            availability = false,
            position = Position.HOME,
            author = "Пушкин А.С.",
            numberOfPages = 320,
            service = service
        )
    )
    addItemBook(
        createBook(
            name = "Алые Плинтуса",
            id = getItemsCounter(),
            availability = true,
            author = "Саша Зелёный",
            service = service
        )
    )
    addItemBook(
        createBook(
            name = "Война и привет",
            id = getItemsCounter(),
            availability = true,
            service = service
        )
    )
}

private fun createNewspapers(service: LibraryService) {
    addItemNewspaper(
        createNewspaper(
            name = "Русская правда",
            id = getItemsCounter(),
            availability = false,
            position = Position.IN_READING_ROOM,
            service = service,
            issueNumber = 794
        )
    )
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

private fun createDisks(service: LibraryService) {
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
        createDisk(
            name = "Просто диск",
            id = getItemsCounter(),
            availability = false,
            position = Position.HOME,
            service = service
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