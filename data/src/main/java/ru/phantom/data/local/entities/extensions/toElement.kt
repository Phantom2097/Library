package ru.phantom.data.local.entities.extensions

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.entities.library.disk.Type.Companion.getEnumType
import ru.phantom.common.entities.library.newspaper.month.Month.Companion.toMonth
import ru.phantom.common.library_service.LibraryElementFactory.createBook
import ru.phantom.common.library_service.LibraryElementFactory.createDisk
import ru.phantom.common.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.data.local.dao.LibraryDB
import ru.phantom.data.local.entities.ItemEntity
import ru.phantom.data.local.entities.ItemEntity.Companion.BOOK
import ru.phantom.data.local.entities.ItemEntity.Companion.DISK
import ru.phantom.data.local.entities.ItemEntity.Companion.NEWSPAPER

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