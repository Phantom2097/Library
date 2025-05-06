package ru.phantom.library.presentation.main

import android.accounts.NetworkErrorException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import ru.phantom.library.data.remote.retrofit.RemoteGoogleBooksRepository
import ru.phantom.library.data.remote.retrofit.RetrofitHelper
import ru.phantom.library.data.repository.DBRepository
import ru.phantom.library.data.repository.ItemsRepository
import ru.phantom.library.data.repository.extensions.SimulateRealRepository
import ru.phantom.library.domain.entities.library.book.Book
import ru.phantom.library.domain.entities.library.disk.Disk
import ru.phantom.library.domain.entities.library.newspaper.Newspaper
import ru.phantom.library.domain.library_service.LibraryElementFactory.createBook
import ru.phantom.library.domain.library_service.LibraryElementFactory.createDisk
import ru.phantom.library.domain.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.library.domain.main_recycler.adapter.AdapterItems
import ru.phantom.library.domain.main_recycler.adapter.AdapterItems.DataItem
import ru.phantom.library.domain.main_recycler.adapter.AdapterItems.LoadItem
import ru.phantom.library.domain.remote.repository.GoogleBooksRepository
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.DEFAULT_SORT
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
 *  @param loadingState Отображает состояние загрузки
 *
 *  @see ru.phantom.library.presentation.selected_item.DetailFragment
 *  @see DetailState
 */
class MainViewModel(
    private val dbRepository: ItemsRepository<BasicLibraryElement> = DBRepository(),
    private val remoteRepository: GoogleBooksRepository = RemoteGoogleBooksRepository(RetrofitHelper.createRetrofit())
) : ViewModel() {

    private val _elements = MutableStateFlow<List<AdapterItems>>(emptyList())
    val elements = _elements.asStateFlow()

    private val _detailState =
        MutableStateFlow<LoadingStateToDetail>(LoadingStateToDetail.Data(DetailState()))
    val detailState = _detailState.asStateFlow()

    // Пока не работает со времён внедрения Room
    private val _scrollToEnd = MutableSharedFlow<Boolean>(replay = 1, extraBufferCapacity = 1)
    val scrollToEnd = _scrollToEnd.asSharedFlow()

    private val _createState = MutableStateFlow<CreateState>(CreateState())
    val createState = _createState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _requestDescription = MutableStateFlow<Pair<String, String>>("" to "")
    val requestDescription = _requestDescription.asStateFlow()

    private var currentStart = START_POSITION
    private var currentEnd = START_POSITION
    private var totalItems = INIT_TOTAL_COUNT

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

    fun updateAuthor(author: String) {
        _requestDescription.update {
            author to it.second
        }
    }

    fun updateTitle(title: String) {
        _requestDescription.update {
            it.first to title
        }
    }

    fun clearRequestDescription() {
        _requestDescription.update {
            "" to ""
        }
    }

    fun getGoogleBooks(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val books = remoteRepository.getGoogleBooks(query)

            delay(1000L)

            _elements.update {
                books.mapNotNull { book -> book?.let { DataItem(it) } }
            }
        }
    }

    private var currentLoadJob: Job? = null

    /**
     * Изначально метод предназначался только для загрузки начальных данных.
     * Но я вижу в нём потенциал обновлять данные при удалении, изменении видимости и добавлении элемента
     */
    private fun loadInitialData(start: Int = START_POSITION, size: Int = INIT_SIZE) {
        currentLoadJob?.cancel()
        currentLoadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                _elements.update { emptyList<AdapterItems>() }
                (dbRepository as? SimulateRealRepository)?.delayLikeRealRepository()
                currentStart = start
                val items = dbRepository.getItems(size, currentStart)

                if (items.isNotEmpty()) {
                    _elements.value = items.map { DataItem(it) }
                            .also { currentEnd = currentStart + it.size }
                }

                paginationLogger("Load data complete", items.size)

            } catch (e: kotlinx.coroutines.CancellationException) {
                Log.i("PAGINATION", "Загрузка элементов отменена", e)
            } finally {
                Log.d("PAGINATION", "${_loadingState.value}")
            }
        }
    }

    private var loadNextJob: Job? = null
    private var loadPrevJob: Job? = null

    fun loadNext() {
        loadPrevJob?.cancel()

        if (loadNextJob != null || currentEnd.toLong() >= totalItems) return

        loadNextJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                startNextLoad()
                (dbRepository as? SimulateRealRepository)?.delayLikeRealRepository() // Задержка в развитии

                val loadCount = min(COUNT_FOR_LOAD, totalItems.toInt() - currentEnd)
                val dropCount = loadCount - (INIT_SIZE - (currentEnd - currentStart))

                val newItems = dbRepository.getItems(
                    loadCount,
                    currentEnd
                )
                    .map { DataItem(it) }

                currentStart = changeToGridSpan(currentStart + dropCount)
                currentEnd += loadCount


                if (newItems.isNotEmpty()) {
                    _elements.update { items ->
                        (items.dropLast(DROP_COUNT) + newItems).drop(
                            changeToGridSpan(dropCount)
                        )
                    }
                }

                paginationLogger("LoadNext complite", dropCount)

            } catch (e: kotlinx.coroutines.CancellationException) {
                excludeLoadItem(dropLastCount = DROP_COUNT)
                // Я решил что тут лучше подойдёт информационный лог
                Log.i("PAGINATION", "Загрузка следующих элементов отменена", e)
            } finally {
                loadNextJob = null
                _loadingState.value = false
            }
        }
    }

    /**
     * Обновляет состояние загрузки и добавляет шиммер [LoadItem] в конце списка,
     *      * при это очищая от других шиммеров
     */
    private fun startNextLoad() {
        _loadingState.update { true }
        _elements.update { items ->
            items + LoadItem
        }
    }

    fun loadPrev() {
        loadNextJob?.cancel()

        if (loadPrevJob != null || currentStart == START_POSITION) return

        loadPrevJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                startPrevLoad()
                (dbRepository as? SimulateRealRepository)?.delayLikeRealRepository() // Задержка

                val loadCount = if (currentStart < COUNT_FOR_LOAD) currentStart else COUNT_FOR_LOAD
                val takeCount = INIT_SIZE - loadCount
                currentStart = changeToGridSpan(currentStart - loadCount)

                val newItems = dbRepository.getItems(
                    loadCount,
                    currentStart
                ).map { DataItem(it) }

                if (newItems.isNotEmpty()) {
                    currentEnd = changeToGridSpan(currentEnd - loadCount)
                    _elements.update { items ->
                        newItems + items.drop(DROP_COUNT).take(takeCount)
                    }
                }

                paginationLogger("LoadPrev complete", takeCount)

            } catch (e: kotlinx.coroutines.CancellationException) {
                excludeLoadItem(dropCount = DROP_COUNT)
                Log.i("PAGINATION", "Загрузка предыдущих элементов отменена", e)
            } finally {
                loadPrevJob = null
                _loadingState.value = false
            }
        }
    }

    private fun excludeLoadItem(dropCount: Int = DROP_DEFAULT, dropLastCount: Int = DROP_DEFAULT) {
        _elements.update { items ->
            items.drop(dropCount).dropLast(dropLastCount)
        }
    }

    /**
     * Обновляет состояние загрузки и добавляет шиммер [LoadItem] в начале списка,
     * при это очищая от других шиммеров
     */
    private fun startPrevLoad() {
        _loadingState.update { true }
        _elements.update { items ->
            listOf(LoadItem) + items
        }
    }

    private fun paginationLogger(message: String, takeCount: Int) {
        Log.d(
            "PAGINATION",
            "$message: ${_elements.value.size} items, takeCount $takeCount\ncurrentStart: $currentStart, currentEnd: $currentEnd"
        )
    }

    /**
     * Для того чтобы сетка не смещалась при количестве элементов не кратном [SPAN_COUNT]
     */
    private fun changeToGridSpan(start: Int): Int =
        ((start + DOP_NUM_FOR_GRID_SPAN) / SPAN_COUNT) * SPAN_COUNT

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

                        (dbRepository as SimulateRealRepository).delayLikeRealRepository()
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

    /**
     * Временная реализация перехода к новому элементу, работает только при изначальном порядке
     */
    private fun updateElements(newItem: BasicLibraryElement) {
        viewModelScope.launch {
            dbRepository.addItems(newItem)
            totalItems = dbRepository.getTotalCount()

            loadInitialData(changeToGridSpan(totalItems.toInt() - INIT_SIZE))
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

    fun updateElementContent(position: Int, newItem: BasicLibraryElement) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.changeItem(position, newItem)

            /*
            Если попробовать обновить состояние во время загрузки новых элементов, естественно
            может вылететь приложение.
             */
            loadInitialData(currentStart, currentEnd - currentStart)
        }
    }

    /*
    Пока такая проверка
     */
    private var lastSortType = DEFAULT_SORT

    fun setSortType(sortState: String) {
        if (sortState != lastSortType) {
            viewModelScope.launch(Dispatchers.IO) {
                (dbRepository as DBRepository).setSortType(sortState)
                loadInitialData()
            }
            lastSortType = sortState
        }
    }

    fun removeElementById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        dbRepository.removeItem(id)

        _elements.update { currentList ->
            currentList.filter { it is DataItem && it.listElement.item.id != id }
        }

        currentEnd--
        totalItems--

        checkIfIsDisplayed(id)
    }

    private fun checkIfIsDisplayed(id: Long) {
        when (val state = _detailState.value) {
            is LoadingStateToDetail.Data -> {
                if (id == state.data.id) {
                    setDetailState()
                }
            }

            else -> return
        }
    }

    companion object {
        const val DOP_NUM_FOR_GRID_SPAN = 1

        private const val START_POSITION = 0
        private const val INIT_TOTAL_COUNT = 0L

        private const val DROP_DEFAULT = 0
        private const val DROP_COUNT = 1

        const val LOAD_THRESHOLD = 10
        const val INIT_SIZE = 48
        const val COUNT_FOR_LOAD = INIT_SIZE / 2
    }
}