package ru.phantom.data.local.repository

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.entities.library.Position
import ru.phantom.common.entities.library.disk.Type.CD
import ru.phantom.common.entities.library.disk.Type.DVD
import ru.phantom.common.entities.library.newspaper.month.Month.JANUARY
import ru.phantom.common.library_service.LibraryElementFactory.createBook
import ru.phantom.common.library_service.LibraryElementFactory.createDisk
import ru.phantom.common.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.common.library_service.LibraryService
import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.models.library.items.disk.DiskImpl
import ru.phantom.common.models.library.items.newspaper.NewspaperImpl
import ru.phantom.common.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.common.library_service.LibraryRepository.getItemsCounter

fun initStartItems(): List<BasicLibraryElement> {
    val service = LibraryService
    return listOf(
        createBooks(service),
        createNewspapers(service),
        createDisks(service)
    ).flatten()
}
/**
 * Создание книг
 */
private fun createBooks(service: LibraryService) = buildList {
    add(
        createBook(
            name = "Котлин для профессионалов",
            author = "Джош Скин, Дэвид Грэнхол, Эндрю Бэйли",
            numberOfPages = 560,
            service = service
        )
    )

    add(
        createBook(
            name = "Маугли",
            availability = false,
            author = "Джозеф Киплинг",
            numberOfPages = 202,
            service = service
        )
    )
    add(
        createBook(
            name = "Kotlin Design Patterns and Best Practices",
            availability = false,
            position = Position.IN_READING_ROOM,
            author = "Alexey Soshin, Anton Arhipov",
            numberOfPages = 356,
            service = service
        )
    )
    add(
        createBook(
            name = "Евгений Онегин",
            availability = false,
            position = Position.HOME,
            author = "Пушкин А.С.",
            numberOfPages = 320,
            service = service
        )
    )
    add(
        createBook(
            name = "Алые Плинтуса",
            id = getItemsCounter(),
            availability = true,
            author = "Саша Зелёный",
            service = service
        )
    )
    add(
        createBook(
            name = "Война и привет",
            id = getItemsCounter(),
            availability = true,
            service = service
        )
    )
}

/**
 * Создание газет
 */
private fun createNewspapers(service: LibraryService) = buildList {
    add(
        createNewspaper(
            name = "Русская правда",
            id = getItemsCounter(),
            availability = false,
            position = Position.IN_READING_ROOM,
            service = service,
            issueNumber = 794
        )
    )
    add(
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
    add(
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
    add(
        NewspaperImpl(
            LibraryItem(
                name = "Русская ложь",
                id = getItemsCounter(),
                availability = true,
            ),
            service
        )
    )
    add(
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

/**
 * Создание дисков
 */
private fun createDisks(service: LibraryService) = buildList {
    add(
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

    add(
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

    add(
        createDisk(
            name = "Просто диск",
            id = getItemsCounter(),
            availability = false,
            position = Position.HOME,
            service = service
        )
    )

    add(
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