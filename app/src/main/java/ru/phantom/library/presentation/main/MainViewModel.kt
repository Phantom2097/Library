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
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation_console.main.createBooks
import ru.phantom.library.presentation_console.main.createDisks
import ru.phantom.library.presentation_console.main.createNewspapers

class MainViewModel : ViewModel() {
    private val _elements = MutableLiveData<List<BasicLibraryElement>>()
    val elements: LiveData<List<BasicLibraryElement>> = _elements

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

    fun removeElement(position: Int) {
        val newList = _elements.value?.toMutableList() ?: mutableListOf()

        if (position in newList.indices) {
            newList.removeAt(position)
        }

        Log.d("Size", "prev size = ${_elements.value?.size}")
        Log.d("Size", "new size = ${newList.size}")

        _elements.value = newList
    }

    /*
     Перенёс функцию создания стартовых элементов во viewModel
     Теперь фича с бесконечным добавлением элементов не работает
     */
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