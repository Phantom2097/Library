package ru.phantom.data.remote.model

import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.library_service.LibraryElementFactory.createBook

object GoogleBooksResponseMapper {

    fun GoogleBooksResponse.toBooks(): List<Book?> {
        return this.items.mapNotNull { item ->
            item.volumeInfo?.toBook()
        }
    }

    private fun VolumeInfo.toBook(): Book? {
        val isbn = industryIdentifiers.firstOrNull { it.type == "ISBN_10" }?.identifier
            ?:industryIdentifiers.firstOrNull()?.identifier ?: return null

        return createBook(
            name = title ?: return null,
            id = isbn.toLongOrNull() ?: return null,
            author = authors.joinToString(", "),
            numberOfPages = pageCount
        )
    }
}