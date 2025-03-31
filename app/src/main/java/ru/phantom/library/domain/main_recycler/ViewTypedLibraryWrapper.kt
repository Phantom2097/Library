package ru.phantom.library.domain.main_recycler

import ru.phantom.library.data.entites.library.items.Itemable
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper as NewspaperLib


sealed class ViewTypedLibraryWrapper {
    data class Book(val book: BookImpl): ViewTypedLibraryWrapper()
    data class Newspaper(val newspaper: NewspaperLib): ViewTypedLibraryWrapper()
    data class Disk(val disk: DiskImpl): ViewTypedLibraryWrapper()

    fun getItemable(): Itemable = when (this) {
        is Book -> book
        is Newspaper -> newspaper
        is Disk -> disk
    }

//    fun ViewTypedLibraryWrapper.copyWrap(): ViewTypedLibraryWrapper = when (this) {
//        is Book -> Book(book.copy())
//        is Disk -> Disk(disk.copy())
//        is Newspaper -> Newspaper(newspaper.copy())
//    }
}