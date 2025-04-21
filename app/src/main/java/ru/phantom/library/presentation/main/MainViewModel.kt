package ru.phantom.library.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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
import ru.phantom.library.presentation.selected_item.CreateState
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.SHOW_TYPE
import ru.phantom.library.presentation.selected_item.DetailState
import ru.phantom.library.presentation.selected_item.LoadingStateToDetail
import kotlin.random.Random

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

    private var errorCounter = ERROR_COUNTER_INIT

    private val _elements = MutableLiveData<List<BasicLibraryElement>>()
    val elements: LiveData<List<BasicLibraryElement>> = _elements

    private val _detailState =
        MutableStateFlow<LoadingStateToDetail>(LoadingStateToDetail.Data(DetailState()))
    val detailState = _detailState.asStateFlow()

    private val _scrollToEnd = MutableLiveData<Boolean>()
    val scrollToEnd: LiveData<Boolean> = _scrollToEnd

    private val _createState = MutableStateFlow<CreateState?>(CreateState())
    val createState = _createState.asStateFlow()

    fun updateType(type: Int) = viewModelScope.launch {
        _createState.emit(CreateState(itemType = type))
    }

    fun updateName(name: String) = viewModelScope.launch {
        _createState.value?.let {
            _createState.emit(it.copy(name = name))
        }
    }

    fun updateId(id: Int) = viewModelScope.launch {
        _createState.value?.let {
            _createState.emit(it.copy(id = id))
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

    fun changeDetailState(element: BasicLibraryElement) = viewModelScope.launch {
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
     * Теперь при отсутствии передаваемого значения возвращает в Default
     */
    fun setDetailState(state: DetailState = DetailState()) = viewModelScope.launch {
        var detailFlag = false
        flow {
            if (state.uiType == SHOW_TYPE) {
                emit(LoadingStateToDetail.Loading)
                delayEmulator()
                detailFlag = errorEmulator()
            }

            if (!detailFlag) {
                Log.d("uitype", "viewModel передаёт state: ${state.uiType}")
                _detailState.emit(LoadingStateToDetail.Data(state))
            }
        }.collect(_detailState)
    }

    fun updateElements(list: List<BasicLibraryElement>) {
        if (list.isEmpty()) {
            viewModelScope.launch {
                _elements.value = itemsRepository.getItems()
            }
        } else {
            val oldList = _elements.value
            _elements.postValue(oldList?.plus(list) ?: list)
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
    fun addNewElement(libraryItem: LibraryItem, elementType: Int) = viewModelScope.launch {
        val element = withContext(Dispatchers.IO) {
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

    fun updateElementContent(position: Int, newItem: BasicLibraryElement) = viewModelScope.launch {
        if (_elements.value == null) {
            _elements.value = itemsRepository.getItems()
        } else {
            val oldList = _elements.value?.toMutableList()

            oldList?.set(position, newItem)

            oldList?.let {
                _elements.value = it
            }
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
        val newList = _elements.value?.toMutableList() ?: mutableListOf()

        if (position in newList.indices) {
            newList.removeAt(position)
        }

        Log.d("Size", "prev size = ${_elements.value?.size}")
        Log.d("Size", "new size = ${newList.size}")

        _elements.value = newList

        launch(Dispatchers.IO) {
            itemsRepository.removeItem(position)
        }
    }

    /**
     * Эмулирует задержку в диапазоне от 100мс до 2000мс
     */
    private suspend fun delayEmulator() {
        val time = Random.nextLong(RANDOM_START, RANDOM_END)
        delay(time)
    }

    /**
     * Эмулирует состояние ошибки каждый 5ый раз
     */
    private suspend fun errorEmulator(): Boolean {
        val isError = ++errorCounter % ERROR_FREQUENCY == ERROR_COUNTER_COMPARE
        return if (isError) {
            _detailState.emit(LoadingStateToDetail.Error())
            errorCounter = ERROR_COUNTER_INIT
            true
        } else {
            false
        }
    }

    private companion object {
        private const val ERROR_COUNTER_INIT = 0
        private const val ERROR_COUNTER_COMPARE = 0
        private const val ERROR_FREQUENCY = 5

        private const val RANDOM_START = 100L
        private const val RANDOM_END = 2000L
    }
}