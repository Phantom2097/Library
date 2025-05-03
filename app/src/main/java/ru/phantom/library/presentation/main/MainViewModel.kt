package ru.phantom.library.presentation.main

import android.accounts.NetworkErrorException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.local.models.library.items.LibraryItem
import ru.phantom.library.data.repository.DBRepository
import ru.phantom.library.data.repository.ItemsRepository
import ru.phantom.library.domain.entities.library.book.Book
import ru.phantom.library.domain.entities.library.disk.Disk
import ru.phantom.library.domain.entities.library.newspaper.Newspaper
import ru.phantom.library.domain.library_service.LibraryElementFactory.createBook
import ru.phantom.library.domain.library_service.LibraryElementFactory.createDisk
import ru.phantom.library.domain.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter.Companion.TYPE_ITEM
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter.Companion.TYPE_LOAD_BOTTOM
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter.Companion.TYPE_LOAD_UP
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SPAN_COUNT
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.SHOW_TYPE
import ru.phantom.library.presentation.selected_item.states.CreateState
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail
import java.util.concurrent.CancellationException
import kotlin.math.min

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
    // Остался старый репозиторий, чтобы эмулировать задержку, он используется только для этого
    private val dbRepository: ItemsRepository<BasicLibraryElement> = DBRepository(
        App.instance.database,
        App.instance
    )
) : ViewModel() {

    private val _elements = MutableStateFlow<List<BasicLibraryElement>>(emptyList())
    val elements = _elements.asStateFlow()

    private val _detailState =
        MutableStateFlow<LoadingStateToDetail>(LoadingStateToDetail.Data(DetailState()))
    val detailState = _detailState.asStateFlow()

    private val _scrollToEnd = MutableSharedFlow<Boolean>(replay = 1, extraBufferCapacity = 1)
    val scrollToEnd = _scrollToEnd.asSharedFlow()

    private val _createState = MutableStateFlow<CreateState>(CreateState())
    val createState = _createState.asStateFlow()

    private val _loadingState = MutableStateFlow(TYPE_ITEM)
    val loadingState = _loadingState.asStateFlow()

    private var currentStart = 0
    private var currentEnd = 0
    private var totalItems = 0L

    fun updateType(type: Int) = viewModelScope.launch {
        _createState.emit(CreateState(itemType = type))
    }

    // Тут вроде и не сложная операция, но не уверен, нужно или нет
    fun updateName(name: String) {
        _createState.update {
            it.copy(name = name)
        }
    }

    fun updateId(id: Long) {
        _createState.update {
            it.copy(id = id)
        }
    }

    fun clearCreate() = viewModelScope.launch {
        _createState.emit(CreateState())
    }

    fun requestScrollToEnd() = viewModelScope.launch {
        _scrollToEnd.emit(true)
    }

    fun resetScrollToEnd() = viewModelScope.launch {
        _scrollToEnd.emit(false)
    }

    fun onItemClicked(element: BasicLibraryElement) {
        changeDetailState(element)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Получаем общее количество элементов
            totalItems = dbRepository.getTotalCount()
            loadInitialData()
        }
    }

    /**
     * Изначально метод предназначался только для загрузки начальных данных.
     * Но я вижу в нём потенциал обновлять данные при удалении, изменении видимости и добавлении элемента
     */
    private fun loadInitialData(start: Int = START_POSITION, size: Int = INIT_SIZE) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentStart = 0
                val items = dbRepository.getItems(INIT_SIZE, currentStart)
                _elements.value = items.also { currentEnd = INIT_SIZE }
                Log.d(
                    "PAGINATION",
                    "Initial load: ${_elements.value.size} items, currentOffset $currentEnd"
                )
            } finally {
                Log.d("PAGINATION", "${_loadingState.value}")
            }
        }
        Log.d("PAGINATION", "${_loadingState.value}")
    }

    private var loadNextJob: Job? = null
    private var loadPrevJob: Job? = null

    fun loadNext() {
        loadPrevJob?.cancel()
        if (loadNextJob != null || currentEnd.toLong() == totalItems) return
        _loadingState.value = TYPE_LOAD_BOTTOM
        Log.d("PAGINATION", "Прошло дальше первой проверки в LoadNext")

        loadNextJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                (dbRepository as DBRepository).delayEmulator()
                val loadCount = min(COUNT_FOR_LOAD, totalItems.toInt() - currentEnd)
                val newItems = dbRepository.getItems(
                    loadCount,
                    currentEnd
                ).also {
                    currentEnd += loadCount
                    currentStart = changeToGridSpan(currentStart + loadCount + 1)
                    Log.d(
                        "PAGINATION",
                        "Next load: ${it.size} items, currentOffset $currentEnd\ncurrentStart: $currentStart, currentEnd: $currentEnd"
                    )
                }
                if (newItems.isNotEmpty()) {
                    _elements.update {
                        _elements.value.drop(changeToGridSpan(loadCount + 1)) + newItems
                    }
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Я решил что тут лучше подойдёт информационный лог
                Log.i("PAGINATION", "Загрузка следующих элементов отменена", e)
            } finally {
                loadNextJob = null
                _loadingState.value = TYPE_ITEM
            }
        }
    }

    fun loadPrev() {
        loadNextJob?.cancel()
        if (loadPrevJob != null || currentStart == 0) return

        _loadingState.value = TYPE_LOAD_UP

        loadPrevJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                (dbRepository as DBRepository).delayEmulator()

                val loadCount = if (currentStart < COUNT_FOR_LOAD) currentStart else COUNT_FOR_LOAD
                currentStart = changeToGridSpan(currentStart - loadCount + 1)


                val newItems = dbRepository.getItems(
                    loadCount,
                    currentStart
                )
                if (newItems.isNotEmpty()) {
                    currentEnd = changeToGridSpan(currentEnd - (loadCount) + 1)
                    _elements.update {
                        newItems + _elements.value.take(INIT_SIZE - loadCount)
                    }
                }
                Log.d(
                    "PAGINATION",
                    "Prev load: ${_elements.value.size} items, currentOffset $currentEnd\ncurrentStart: $currentStart, currentEnd: $currentEnd"
                )
            } catch (e: kotlinx.coroutines.CancellationException) {
                Log.i("PAGINATION", "Загрузка предыдущих элементов отменена", e)
            } finally {
                loadPrevJob = null
                _loadingState.value = TYPE_ITEM
            }
        }
    }

    /**
     * Для того чтобы сетка не смещалась при количестве элементов не кратном [SPAN_COUNT]
     */
    private fun changeToGridSpan(start: Int): Int = (start / SPAN_COUNT) * SPAN_COUNT

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
            try {
                flow {
                    if (state.uiType == SHOW_TYPE) {
                        emit(LoadingStateToDetail.Loading)

                        (dbRepository as DBRepository).simulateRealRepository()
                        Log.d("uitype", "viewModel передаёт state: ${state.uiType}")

                        emit(LoadingStateToDetail.Data(state))
                    }
                    emit(LoadingStateToDetail.Data(state))
                }
                    .catch { e ->
                        handleRepositoryErrors(e)
                    }
                    .collect(_detailState)
            } catch (e: CancellationException) {
                Log.w("DetailState", "Операция перехода отменена out flow", e)
            }
        }
    }

    private suspend fun handleRepositoryErrors(e: Throwable) {
        val errorState = when (e) {
            is NetworkErrorException -> {
                Log.w("DetailState", "Ошибка сети", e)
                LoadingStateToDetail.Error("Ошибка сети, проверьте подключение")
            }

            else -> {
                Log.w("DetailState", "Непредвиденная ошибка", e)
                LoadingStateToDetail.Error(e.message ?: "Unknown error")
            }
        }
        _detailState.emit(errorState)
    }

    private fun updateElements(newItem: BasicLibraryElement) {
        viewModelScope.launch {
            dbRepository.addItems(newItem)

            Log.d("ScrollState", "Эмитется новое значение")
            requestScrollToEnd()
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

        element?.let {
            updateElements(it)
        }
    }

    fun updateElementContent(position: Int, newItem: BasicLibraryElement) = viewModelScope.launch(
        Dispatchers.IO
    ) {
        dbRepository.changeItem(position, newItem)
    }

    fun setSortType(sortState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            (dbRepository as DBRepository).setSortType(sortState)
            loadInitialData()
        }
    }

    fun removeElementById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        dbRepository.removeItem(id)
        when (val state = _detailState.value) {
            is LoadingStateToDetail.Data -> {
                if (id == state.data.id) {
                    setDetailState()
                }
            }
            else -> return@launch
        }
    }

    companion object {
        const val START_POSITION = 0
        const val LOAD_THRESHOLD = 10
        const val INIT_SIZE = 48
        const val COUNT_FOR_LOAD = INIT_SIZE / 2
//        const val SHIMMER_TIME = 500L
    }
}