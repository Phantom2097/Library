package ru.phantom.library.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

/**
 *  Вью модель
 *  @param elements хранит список всех элементов
 *  @param detailState хранит состояние
 *  @param itemClickEvent нужен для обработки нажатия на элемент в списке
 *  @param scrollToEnd хранит состояние положения адаптера списка,
 *  пока используется для проматывания вниз
 *
 *  @see ru.phantom.library.presentation.selected_item.DetailFragment
 *  @see DetailState
 */
class MainViewModel : ViewModel() {

    init {
        createItems()
    }

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

    /**
     * Теперь при отсутствии передаваемого значения возвращает в Default
     */
    fun setDetailState(state: DetailState = DetailState()) {
        _detailState.value = state
    }

    fun updateElements(list: List<BasicLibraryElement>) {
        val oldList = _elements.value
        _elements.postValue(oldList?.plus(list) ?: list)
    }

    /**
     * Добавляет новый элемент в соответствующую коллекцию
     *
     * @param libraryItem принимает созданный объект
     * @param elementType тип элемента, который нужно создать (Книга, Газета, Диск)
     *
     * @see LibraryItem
     */
    fun addNewElement(libraryItem: LibraryItem, elementType: Int) = viewModelScope.launch {
        val element = withContext(Dispatchers.Default) {
            when (elementType) {
                BOOK_IMAGE -> createBook(libraryItem)
                NEWSPAPER_IMAGE -> createNewspaper(libraryItem)
                DISK_IMAGE -> createDisk(libraryItem)
                else -> null
            }
        }

        _scrollToEnd.value = true

        element?.let {
            updateElements(listOf(it))
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
            setDetailState()
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