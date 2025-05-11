package ru.phantom.library.presentation.google_books

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.remote.retrofit.RemoteGoogleBooksRepository
import ru.phantom.library.data.remote.retrofit.RetrofitHelper
import ru.phantom.library.domain.entities.library.book.Book
import ru.phantom.library.domain.main_recycler.adapter.events.ItemClickEvent
import ru.phantom.library.domain.main_recycler.adapter.events.ItemUpdateHandler
import ru.phantom.library.domain.remote.repository.GoogleBooksRepository

class GoogleBooksViewModel(
    private val remoteRepository: GoogleBooksRepository = RemoteGoogleBooksRepository(RetrofitHelper.createRetrofit())
) : ViewModel(), ItemClickEvent, ItemUpdateHandler {

    private val _elements = MutableStateFlow<List<Book>>(emptyList())
    val elements = _elements.asStateFlow()

    private val _requestDescription = MutableStateFlow<Pair<String, String>>("" to "")
    val requestDescription = _requestDescription.asStateFlow()


    override fun onItemClick(element: BasicLibraryElement) {
        Unit
    }

    override fun onItemLongClick(
        context: Context?,
        element: BasicLibraryElement
    ) : BasicLibraryElement {
        TODO()
    }

    override fun onItemSwiped(elementId: Long) {
        Unit
    }

    override fun updateElement(
        position: Int,
        newItem: BasicLibraryElement
    ) {
        TODO("Not yet implemented")
    }

    override fun updateDetailStateIfNeeded(newItem: BasicLibraryElement) {
        TODO("Not yet implemented")
    }


}