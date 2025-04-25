package ru.phantom.library.data.repository.book

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import ru.phantom.library.data.local.entities.extensions.toBook
import ru.phantom.library.data.local.entities.extensions.toEntity
import ru.phantom.library.data.repository.ItemsRepository
import ru.phantom.library.domain.entities.library.book.Book
import ru.phantom.library.presentation.main.App

class BookRepository(context: Context) : ItemsRepository<Book> {
    private val db = (context.applicationContext as App).database

    private val booksFlow = MutableStateFlow<List<Book>>(emptyList())
    override suspend fun addItems(item: Book) {
        val (newItem, newBook) = item.toEntity()
        db.itemDao().insertItem(newItem)
        db.bookDao().insertBook(newBook)
    }

    override suspend fun removeItem(position: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getItems(): Flow<List<Book>> {
        return flow { db.itemDao().getItems().map { it.toBook(db.bookDao().getBookInfoById(it.id)) } }
    }

    override suspend fun changeItem(
        position: Int,
        newItem: Book
    ) {
        TODO("Not yet implemented")
    }


}