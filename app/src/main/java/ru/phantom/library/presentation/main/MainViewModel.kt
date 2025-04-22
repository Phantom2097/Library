package ru.phantom.library.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.book.Book
import ru.phantom.library.data.entites.library.items.disk.Disk
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.data.repository.ItemsRepository
import ru.phantom.library.data.repository.ItemsRepositoryImpl
import ru.phantom.library.domain.library_service.LibraryElementFactory.createBook
import ru.phantom.library.domain.library_service.LibraryElementFactory.createDisk
import ru.phantom.library.domain.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.SHOW_TYPE
import ru.phantom.library.presentation.selected_item.states.CreateState
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail

/**
 *  Вью модель
 *  @param elements хранит список всех элементов
 *  @param detailState хранит состояние
 *  @param scrollToEnd хранит состояние положения адаптера списка,
 *  пока используется для проматывания вниз
 *
 *  @see ru.phantom.library.presentation.selected_item.DetailFragment
 *  @see DetailState
 */
class MainViewModel(
    private val itemsRepository: ItemsRepository<BasicLibraryElement> = ItemsRepositoryImpl()
) : ViewModel() {

    private val _elements = MutableStateFlow<List<BasicLibraryElement>>(emptyList())
    val elements = _elements.asStateFlow()

    private val _detailState =
        MutableStateFlow<LoadingStateToDetail>(LoadingStateToDetail.Data(DetailState()))
    val detailState = _detailState.asStateFlow()

    private val _scrollToEnd = MutableStateFlow<Boolean>(false)
    val scrollToEnd = _scrollToEnd.asStateFlow()

    private val _createState = MutableStateFlow<CreateState>(CreateState())
    val createState = _createState.asStateFlow()

    fun updateType(type: Int) = viewModelScope.launch {
        _createState.emit(CreateState(itemType = type))
    }

    // Тут вроде и не сложная операция, но не уверен, нужно или нет
    fun updateName(name: String) = viewModelScope.launch(Dispatchers.IO) {
        _createState.update {
            it.copy(name = name)
        }
    }

    fun updateId(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _createState.update {
            it.copy(id = id)
        }
    }

    fun clearCreate() = viewModelScope.launch {
        _createState.emit(CreateState())
    }

    fun scrollToEndReset() {
        _scrollToEnd.value = false
    }

    fun onItemClicked(element: BasicLibraryElement) {
        changeDetailState(element)
    }

    private fun changeDetailState(element: BasicLibraryElement) = viewModelScope.launch {
        val image = withContext(Dispatchers.IO) {
            when (element) {
                is Book -> BOOK_IMAGE
                is Newspaper -> NEWSPAPER_IMAGE
                is Disk -> DISK_IMAGE
                else -> DEFAULT_IMAGE
            }
        }
        setDetailState(
            DetailState(
                uiType = SHOW_TYPE,
                name = element.item.name,
                id = element.item.id,
                image = image,
                description = element.fullInformation()
            )
        )
    }

    /**
     * Хранит джобу для отмены перехода на DetailFragment при быстром обращении
     */
    private var detailStateJob: Job? = null

    /**
     * Теперь при отсутствии передаваемого значения возвращает в Default
     */
    fun setDetailState(state: DetailState = DetailState()) {
        detailStateJob?.cancel()
        detailStateJob = viewModelScope.launch(Dispatchers.IO) {
            flow {
                if (state.uiType == SHOW_TYPE) {
                    emit(LoadingStateToDetail.Loading)
                    itemsRepository.delayEmulator()
                    itemsRepository.errorEmulator()
                    Log.d("uitype", "viewModel передаёт state: ${state.uiType}")
                    emit(LoadingStateToDetail.Data(state))
                }
                emit(LoadingStateToDetail.Data(state))
            }.catch { e ->
                _detailState.emit(LoadingStateToDetail.Error(e.message))
            }.collect(_detailState)
        }
    }

    fun updateElements(list: List<BasicLibraryElement>) = viewModelScope.launch {
        if (list.isEmpty()) {
            _elements.value = itemsRepository.getItems()
        } else {
            itemsRepository.addItems(list[0])

            _elements.update {
                itemsRepository.getItems()
            }
        }
    }

    /**
     * Добавляет новый элемент в соответствующую коллекцию
     *
     * @param libraryItem принимает созданный объект
     * @param elementType тип элемента, который нужно создать (Книга, Газета, Диск)
     *
     * @see LibraryItem
     */
    fun addNewElement(libraryItem: LibraryItem, elementType: Int) {
        val element = when (elementType) {
            BOOK_IMAGE -> createBook(libraryItem)
            NEWSPAPER_IMAGE -> createNewspaper(libraryItem)
            DISK_IMAGE -> createDisk(libraryItem)
            else -> null
        }

        _scrollToEnd.value = true

        element?.let {
            updateElements(listOf(it))
        }
    }

    fun updateElementContent(position: Int, newItem: BasicLibraryElement) = viewModelScope.launch {
        itemsRepository.changeItem(position, newItem)
        _elements.update {
            itemsRepository.getItems()
        }
    }

    fun selectedRemove(element: BasicLibraryElement) {
        when (val state = _detailState.value) {
            is LoadingStateToDetail.Data -> {
                if (element.item.id == state.data.id
                    && element.item.name == state.data.name
                ) {
                    setDetailState()
                }
            }

            else -> return
        }
    }

    fun removeElement(position: Int) = viewModelScope.launch {
        itemsRepository.removeItem(position)
        _elements.update {
            itemsRepository.getItems()
        }
    }
}