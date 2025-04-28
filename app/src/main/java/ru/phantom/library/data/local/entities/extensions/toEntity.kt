package ru.phantom.library.data.local.entities.extensions

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.phantom.library.data.dao.LibraryDB
import ru.phantom.library.data.local.entities.BookEntity
import ru.phantom.library.data.local.entities.DiskEntity
import ru.phantom.library.data.local.entities.ItemEntity
import ru.phantom.library.data.local.entities.ItemEntity.Companion.BOOK
import ru.phantom.library.data.local.entities.ItemEntity.Companion.DISK
import ru.phantom.library.data.local.entities.ItemEntity.Companion.NEWSPAPER
import ru.phantom.library.data.local.entities.NewspaperEntity
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.local.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.library.domain.entities.library.book.Book
import ru.phantom.library.domain.entities.library.disk.Disk
import ru.phantom.library.domain.entities.library.newspaper.Newspaper

suspend fun BasicLibraryElement.toEntity(db: LibraryDB) {
    withContext(Dispatchers.IO) {
        when (this@toEntity) {
            is Book -> toBookEntity(db, toItemEntity())
            is Disk -> toDiskEntity(db, toItemEntity())
            is Newspaper -> toNewspaperEntity(db, toItemEntity())
            else -> Log.d("DB", "Ничего не вставлено, потому что кто-то лох")
        }
    }
}

private suspend fun Book.toBookEntity(
    db: LibraryDB, itemEntity: ItemEntity
) {
    val id = db.itemDao().insertItem(itemEntity.copy(itemType = BOOK))
    Log.d("DB", "Inserting book with id: $id")

    val book = BookEntity(
        id = id, author = author.joinToString(", "), numberOfPages = numberOfPages
    )
    db.bookDao().insertBook(book)
}

private suspend fun Disk.toDiskEntity(
    db: LibraryDB, itemEntity: ItemEntity
) {
    val id = db.itemDao().insertItem(itemEntity.copy(itemType = DISK))
    Log.d("DB", "Inserting disk with id: $id")

    val disk = DiskEntity(
        id = id, type = type.getType()
    )
    db.diskDao().insertDisk(disk)
}

private suspend fun Newspaper.toNewspaperEntity(
    db: LibraryDB, itemEntity: ItemEntity
) {
    val id = db.itemDao().insertItem(itemEntity.copy(itemType = NEWSPAPER))
    Log.d("DB", "Inserting newspaper with id: $id")

    val newspaperElement = this as? NewspaperWithMonthImpl
    val newspaper = NewspaperEntity(
        id = id, issueNumber = issueNumber, month = newspaperElement?.issueMonth?.getMonth()
    )
    db.newspaperDao().insertNewspaper(newspaper)
}

fun BasicLibraryElement.toItemEntity(): ItemEntity = ItemEntity(
    name = item.name, availability = item.availability, time = System.currentTimeMillis()
)
