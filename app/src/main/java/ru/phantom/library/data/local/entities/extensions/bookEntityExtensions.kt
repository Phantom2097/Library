package ru.phantom.library.data.local.entities.extensions

import ru.phantom.library.data.local.entities.BookEntity
import ru.phantom.library.data.local.entities.ItemEntity
import ru.phantom.library.data.local.models.library.items.LibraryItem
import ru.phantom.library.data.local.models.library.items.book.BookImpl
import ru.phantom.library.domain.entities.library.book.Book
import ru.phantom.library.domain.library_service.LibraryService

fun Book.toEntity(): Pair<ItemEntity, BookEntity> {
    return Pair(
        ItemEntity(
            id = item.id,
            name = item.name,
            availability = item.availability
        ),
        BookEntity(
            ownerId = item.id,
            author = author.joinToString(", "),
            numberOfPages = numberOfPages
        )
    )
}

fun ItemEntity.toBook(bookInfo: BookEntity): Book {
    val item = LibraryItem(
        name = this.name,
        id = this.id,
        availability = this.availability
    )

    val book = BookImpl(
        item = item,
        service = LibraryService,
        author = bookInfo.author.split(", "),
        numberOfPages = bookInfo.numberOfPages
    )

    return book
}