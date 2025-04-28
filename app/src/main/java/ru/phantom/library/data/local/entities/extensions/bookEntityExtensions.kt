package ru.phantom.library.data.local.entities.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.phantom.library.data.dao.LibraryDB
import ru.phantom.library.data.local.entities.BookEntity
import ru.phantom.library.data.local.entities.ItemEntity
import ru.phantom.library.data.local.models.library.items.LibraryItem
import ru.phantom.library.data.local.models.library.items.book.BookImpl
import ru.phantom.library.domain.entities.library.book.Book
import ru.phantom.library.domain.library_service.LibraryService

fun Book.toEntity(db: LibraryDB) {
    CoroutineScope(Dispatchers.IO).launch {
        val itemEntity = ItemEntity(
            name = item.name,
            availability = item.availability,
            time = System.currentTimeMillis()
        )
        val id = db.itemDao().insertItem(itemEntity)
        BookEntity(
            id = id,
            author = author.joinToString(", "),
            numberOfPages = numberOfPages
        )
    }

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