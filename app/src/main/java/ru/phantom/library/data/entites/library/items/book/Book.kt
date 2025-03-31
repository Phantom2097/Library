package ru.phantom.library.data.entites.library.items.book

import ru.phantom.library.data.entites.library.Readable
import ru.phantom.library.data.entites.library.Showable
import ru.phantom.library.data.entites.library.items.Itemable
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.domain.library_service.LibraryService

abstract class Book (
    open val item: LibraryItem,
    open val libraryService: LibraryService,
) :
    Itemable,
    Readable,
    Showable
{
    abstract var author: String
    abstract var numberOfPages: Int?
}