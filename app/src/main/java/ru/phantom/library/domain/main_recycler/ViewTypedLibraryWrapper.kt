package ru.phantom.library.domain.main_recycler

import ru.phantom.library.data.entites.library.items.Itemable
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.newspaper.NewspaperImpl


sealed class ViewTypedLibraryWrapper {
    data class Book(val book: BookImpl): ViewTypedLibraryWrapper()
    data class Newspaper(val newspaper: NewspaperImpl): ViewTypedLibraryWrapper()
    data class Disk(val disk: DiskImpl): ViewTypedLibraryWrapper()

    fun getItemable(): Itemable = when (this) {
        is Book -> book
        is Newspaper -> newspaper
        is Disk -> disk
    }
}