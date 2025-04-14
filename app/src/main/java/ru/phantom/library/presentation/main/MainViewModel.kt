package ru.phantom.library.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.repository.LibraryRepository
import ru.phantom.library.domain.library_service.LibraryElementFactory.createBook
import ru.phantom.library.domain.library_service.LibraryElementFactory.createDisk
import ru.phantom.library.domain.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.library.domain.library_service.LibraryService
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.DetailState
import ru.phantom.library.presentation_console.main.createBooks
import ru.phantom.library.presentation_console.main.createDisks
import ru.phantom.library.presentation_console.main.createNewspapers

class MainViewModel : ViewModel() {
    private val _elements = MutableLiveData<List<BasicLibraryElement>>()
    val elements: LiveData<List<BasicLibraryElement>> = _elements

    private val _detailState = MutableLiveData<DetailState>(DetailState())
    val detailState: LiveData<DetailState> = _detailState

    private val _itemClickEvent = MutableLiveData<BasicLibraryElement?>()
    val itemClickEvent: LiveData<BasicLibraryElement?> = _itemClickEvent

    private val _scrollToEnd = MutableLiveData<Boolean>()
    val scrollToEnd: LiveData<Boolean> = _scrollToEnd

    fun scrollToEndReset() {
        _scrollToEnd.value = false
    }

    fun onItemClicked(element: BasicLibraryElement) {
        _itemClickEvent.value = element
    }

    fun reloadListener() {
        _itemClickEvent.value = null
    }



    fun setDetailState(state: DetailState) {
        _detailState.value = state
    }

    init {
        createItems()
    }

    fun updateElements(list: List<BasicLibraryElement>) {
        val oldList = _elements.value
        _elements.postValue(oldList?.plus(list) ?: list)
    }

    fun addNewElement(libraryItem: LibraryItem, elementType: Int) {
        val element = when (elementType) {
            BOOK_IMAGE -> createBook(libraryItem)
            NEWSPAPER_IMAGE -> createNewspaper(libraryItem)
            DISK_IMAGE -> createDisk(libraryItem)
            else -> null
        }

        _scrollToEnd.value = true

        element?.let {
            updateElements(listOf(element))
        }
    }

    fun updateElementContent(position: Int, newItem: BasicLibraryElement) {
        val oldList = _elements.value?.toMutableList()

        oldList?.set(position, newItem)

        oldList?.let {
            _elements.value = it
        }
    }

    fun selectedRemove(element: BasicLibraryElement) {
        if (element.item.id == _detailState.value?.id) {
            _detailState.value = DetailState()
        }
    }

    fun removeElement(position: Int) {
        val newList = _elements.value?.toMutableList() ?: mutableListOf()

        if (position in newList.indices) {
            newList.removeAt(position)
        }

        Log.d("Size", "prev size = ${_elements.value?.size}")
        Log.d("Size", "new size = ${newList.size}")

        _elements.value = newList
    }

    private fun createItems() {
        if (_elements.value == null) {
            // Создаю элементы для отображения
            val libraryService = LibraryService

            createBooks(libraryService)
            createNewspapers(libraryService)
            createDisks(libraryService)

            val items = mutableListOf<BasicLibraryElement>().apply {
                addAll(LibraryRepository.getBooksInLibrary())
                addAll(LibraryRepository.getNewspapersInLibrary())
                addAll(LibraryRepository.getDisksInLibrary())
            }

            updateElements(items)
        }
    }
}