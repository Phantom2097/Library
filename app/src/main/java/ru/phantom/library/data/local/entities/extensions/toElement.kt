package ru.phantom.library.data.local.entities.extensions

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.phantom.library.data.dao.LibraryDB
import ru.phantom.library.data.local.entities.ItemEntity
import ru.phantom.library.data.local.entities.ItemEntity.Companion.BOOK
import ru.phantom.library.data.local.entities.ItemEntity.Companion.DISK
import ru.phantom.library.data.local.entities.ItemEntity.Companion.NEWSPAPER
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.domain.entities.library.disk.Type.Companion.getEnumType
import ru.phantom.library.domain.entities.library.newspaper.month.Month.Companion.toMonth
import ru.phantom.library.domain.library_service.LibraryElementFactory.createBook
import ru.phantom.library.domain.library_service.LibraryElementFactory.createDisk
import ru.phantom.library.domain.library_service.LibraryElementFactory.createNewspaper

suspend fun ItemEntity.toElement(db: LibraryDB) : BasicLibraryElement? {
    return withContext(Dispatchers.IO) {
        when (itemType) {
            BOOK -> {
                val book = db.bookDao().getBookInfoById(id)
                createBook(
                    name = name,
                    id = id,
                    availability = availability,
                    author = book.author,
                    numberOfPages = book.numberOfPages
                )
            }

            DISK -> {
                val disk = db.diskDao().getDiskInfoById(id)
                createDisk(
                    name = name,
                    id = id,
                    availability = availability,
                    type = getEnumType(disk.type)
                )
            }

            NEWSPAPER -> {
                val newspaper = db.newspaperDao().getNewspaperInfoById(id)
                createNewspaper(
                    name = name,
                    id = id,
                    availability = availability,
                    issueNumber = newspaper.issueNumber,
                    issueMonth = toMonth(newspaper.month)
                )
            }

            else -> {
                Log.d("DB", "Такого не должно быть")
                null
            }
        }
    }
}