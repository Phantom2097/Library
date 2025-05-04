package ru.phantom.library.data.local.entities.extensions

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import ru.phantom.library.presentation.main.App

object ToEntityMappers {

    private val db = App.database

    suspend fun BasicLibraryElement.toEntity() {
        withContext(Dispatchers.IO) {
            when (this@toEntity) {
                is Book -> toBookEntity(toItemEntity())
                is Disk -> toDiskEntity(toItemEntity())
                is Newspaper -> toNewspaperEntity(toItemEntity())
                else -> Log.d("DB", "Ничего не вставлено(((")
            }
        }
    }

    private fun BasicLibraryElement.toItemEntity(): ItemEntity = ItemEntity(
        name = item.name, availability = item.availability, time = System.currentTimeMillis()
    )

    private suspend fun Book.toBookEntity(
        itemEntity: ItemEntity
    ) {
        val item = itemEntity.copy(itemType = BOOK)
        Log.d("DB", "Inserting book with id: ${item.id}")

        val book = BookEntity(
            author = author.joinToString(", "), numberOfPages = numberOfPages
        )
        db.insertDao().insertItemBook(item, book)
    }

    private suspend fun Disk.toDiskEntity(
        itemEntity: ItemEntity
    ) {
        val item = itemEntity.copy(itemType = DISK)
        Log.d("DB", "Inserting disk with id: ${item.id}")

        val disk = DiskEntity(
            type = type.getType()
        )
        db.insertDao().insertItemDisk(item, disk)
    }

    private suspend fun Newspaper.toNewspaperEntity(
        itemEntity: ItemEntity
    ) {
        val item = itemEntity.copy(itemType = NEWSPAPER)
        Log.d("DB", "Inserting newspaper with id: ${item.id}")

        val newspaperElement = this as? NewspaperWithMonthImpl
        val newspaper = NewspaperEntity(
            issueNumber = issueNumber, month = newspaperElement?.issueMonth?.getMonth()
        )
        db.insertDao().insertItemNewspaper(item, newspaper)
    }
}