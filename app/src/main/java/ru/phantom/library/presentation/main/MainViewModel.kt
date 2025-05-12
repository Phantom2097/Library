package ru.phantom.library.presentation.main

import android.accounts.NetworkErrorException
import android.content.Context
import android.util.Log
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.local.models.library.items.LibraryItem
import ru.phantom.library.data.local.models.library.items.book.BookImpl
import ru.phantom.library.data.local.models.library.items.disk.DiskImpl
import ru.phantom.library.data.local.models.library.items.newspaper.NewspaperImpl
import ru.phantom.library.data.local.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
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
import ru.phantom.library.domain.main_recycler.adapter.events.ItemClickEvent
import ru.phantom.library.domain.main_recycler.adapter.events.ItemUpdateHandler
import ru.phantom.library.domain.remote.repository.GoogleBooksRepository
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.DEFAULT_SORT
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.SHOW_TYPE
import ru.phantom.library.presentation.selected_item.states.CreateState
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail
import java.util.concurrent.CancellationException

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
) : ViewModel(), ItemClickEvent, ItemUpdateHandler {

    private val _elements = MutableStateFlow<List<AdapterItems>>(emptyList())
    val elements = _elements.asStateFlow()

    private val _screenModeState = MutableStateFlow<DisplayStates>(DisplayStates.MY_LIBRARY)
    val screenModeState = _screenModeState.asStateFlow()

    private val _detailState =
        MutableStateFlow<LoadingStateToDetail>(LoadingStateToDetail.Data(DetailState()))
    val detailState = _detailState.asStateFlow()

    // Пока не работает со времён внедрения Room
    private val _scrollToEnd = MutableSharedFlow<Boolean>(replay = 1, extraBufferCapacity = 1)
    val scrollToEnd = _scrollToEnd.asSharedFlow()

    private val _createState = MutableStateFlow<CreateState>(CreateState())
    val createState = _createState.asStateFlow()

    private val _loadingState = MutableStateFlow(LOADING_STATE_DEFAULT)
    val loadingState = _loadingState.asStateFlow()

    private val _requestDescription = MutableStateFlow<Pair<String, String>>("" to "")
    val requestDescription = _requestDescription.asStateFlow()

    private val _errorRequest = MutableStateFlow<String?>(null)
    val errorRequest = _errorRequest.asStateFlow()

    private var currentPage = PAGE_START_POSITION
    private var totalItems = INIT_TOTAL_COUNT
    private val totalPages get() = (totalItems + COUNT_FOR_LOAD - 1) / COUNT_FOR_LOAD

    fun updateType(type: Int) = viewModelScope.launch {
        _createState.emit(CreateState(itemType = type))
    }

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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Получаем общее количество элементов
            totalItems = dbRepository.getTotalCount()
            loadElements()
        }
    }

    fun changeMainScreen(displayStates: DisplayStates) {
        _screenModeState.update { displayStates }
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
            val result = remoteRepository.getGoogleBooks(query)

            result.fold(
                onSuccess = { books ->
                    _elements.update {
                        books.mapNotNull { book -> book?.let { DataItem(it) } }
                    }
                },
                onFailure = { exception ->
                    _errorRequest.update { exception.message }
                }
            )
            loadingJob?.cancel()
        }
    }

    private var loadingJob: Job? = null

    private fun loadElements(limit: Int = INIT_SIZE, offset: Int = PAGE_START_POSITION) {
        loadingJob?.cancel()
        currentPage = offset / COUNT_FOR_LOAD
        loadingJob = viewModelScope.launch {
            try {
                dbRepository.getItems(limit, offset)
                    .map { list ->
                        list.map { DataItem(it) }
                    }
                    .catch { e ->
                        _elements.value = emptyList()
                    }
                    .flowOn(Dispatchers.IO)
                    .collect { adapterItems ->
                        _elements.update {
                            adapterItems
                        }
                    }
            } catch (e: kotlinx.coroutines.CancellationException) {
                paginationLogger("Загрузка прерывается, заменяется offset", error = e)
            } finally {
                _loadingState.value = LOADING_STATE_DEFAULT
            }
        }
    }

    private var loadNextJob: Job? = null
    private var loadPrevJob: Job? = null

    fun loadNext() {
        loadPrevJob?.cancel()
        if (loadNextJob != null || currentPage >= totalPages - DISPLAYED_PAGES) return

        startNextLoad()

        loadNextJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                (dbRepository as? SimulateRealRepository)?.delayLikeRealRepository()

                currentPage++

                loadElements(offset = currentPage * COUNT_FOR_LOAD)
            } catch (e: CancellationException) {
                Log.i("PAGINATION", "Загрузка следующих элементов отменена", e)
                excludeLoadItem(dropLastCount = DROP_COUNT)
                _loadingState.value = LOADING_STATE_DEFAULT
            } finally {
                loadNextJob = null
            }
        }
    }

    fun loadPrev() {
        loadNextJob?.cancel()
        if (loadPrevJob != null || currentPage <= PAGE_START_POSITION) return

        startPrevLoad()

        loadPrevJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                (dbRepository as? SimulateRealRepository)?.delayLikeRealRepository()

                currentPage--

                loadElements(offset = currentPage * COUNT_FOR_LOAD)
            } catch (e: kotlinx.coroutines.CancellationException) {
                Log.i("PAGINATION", "Загрузка прошлых элементов отменена", e)
                excludeLoadItem(dropCount = DROP_COUNT)
            } finally {
                _loadingState.value = LOADING_STATE_DEFAULT
                loadPrevJob = null
            }
        }
    }

    fun clearList() {
        _elements.update { emptyList() }
    }

    fun loadCurrent() {
        viewModelScope.launch(Dispatchers.IO) {
            loadElements(offset = currentPage * COUNT_FOR_LOAD)
        }
    }

    /**
     * Обновляет состояние загрузки и добавляет шиммер [LoadItem] в конце списка,
     *      * при это очищая от других шиммеров
     */
    private fun startNextLoad() {
        _loadingState.update { LOADING_STATE_NEXT }
        _elements.update { items ->
            items + LoadItem
        }
    }

    /**
     * Обновляет состояние загрузки и добавляет шиммер [LoadItem] в начале списка,
     * при это очищая от других шиммеров
     */
    private fun startPrevLoad() {
        _loadingState.update { LOADING_STATE_PREV }
        _elements.update { items ->
            listOf(LoadItem) + items
        }
    }

    private fun excludeLoadItem(dropCount: Int = DROP_DEFAULT, dropLastCount: Int = DROP_DEFAULT) {
        _elements.update { items ->
            items.drop(dropCount).dropLast(dropLastCount)
        }
    }


    private fun paginationLogger(
        message: String,
        takeCount: Int? = null,
        error: Exception? = null
    ) {
        Log.d(
            "PAGINATION",
            "$message: ${_elements.value.size} items, takeCount $takeCount\ncurrentPage = $currentPage",
            error
        )
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
            try {
                flow {
                    if (state.uiType == SHOW_TYPE) {
                        emit(LoadingStateToDetail.Loading)

                        (dbRepository as SimulateRealRepository).simulateRealRepository()
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

    /*
    Пока такая проверка
     */
    private var lastSortType = DEFAULT_SORT

    fun setSortType(sortState: String) {
        if (sortState != lastSortType) {
            viewModelScope.launch(Dispatchers.IO) {
                (dbRepository as DBRepository).setSortType(sortState)
                loadElements()
            }
            lastSortType = sortState
        }
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

    override fun onItemClick(element: BasicLibraryElement) {
        changeDetailState(element)
    }

    override fun onItemLongClick(
        context: Context?,
        element: BasicLibraryElement
    ): BasicLibraryElement {
        return when (_screenModeState.value) {
            DisplayStates.MY_LIBRARY -> changeAvailabilityElement(element, context)
            DisplayStates.GOOGLE_BOOKS -> {
                context?.let {
                    var name = element.item.name
                    if (name.length > TOAST_MAX_NAME_LENGTH) name = name.substring(
                        0,
                        TOAST_MAX_NAME_LENGTH - TOAST_THREE_DOTS_LENGTH
                    ) + "..."
                    makeText(
                        context,
                        "Книга $name Добавлена в библиотеку",
                        LENGTH_SHORT
                    ).show()
                }
                element
            }
        }
    }

    private fun changeAvailabilityElement(
        element: BasicLibraryElement,
        context: Context?
    ): BasicLibraryElement {
        val newLibraryItem = element.item.copy(availability = !element.item.availability)
        val newItem = when (element) {
            is BookImpl -> element.copy(item = newLibraryItem)
            is DiskImpl -> element.copy(item = newLibraryItem)
            is NewspaperImpl -> element.copy(item = newLibraryItem)
            is NewspaperWithMonthImpl -> element.copy(item = newLibraryItem)
            else -> throw IllegalArgumentException("Неверный тип элемента")
        }

        Log.d(
            "Availability",
            "prev availability = ${element.item.availability}"
        )
        Log.d(
            "Availability",
            "new availability = ${newItem.item.availability}"
        )

        context?.let {
            makeText(
                context,
                newItem.briefInformation(),
                LENGTH_SHORT
            ).show()
        }
        return newItem
    }


    override fun onItemSwiped(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.removeItem(id)

            _elements.update { currentList ->
                currentList.filter { it is DataItem && it.listElement.item.id != id }
            }

            totalItems--

            checkIfIsDisplayed(id)
        }
    }

    override fun updateElement(
        position: Int,
        newItem: BasicLibraryElement
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            when (_screenModeState.value) {
                DisplayStates.MY_LIBRARY -> dbRepository.changeItem(
                    position + currentPage * COUNT_FOR_LOAD,
                    newItem
                )
                DisplayStates.GOOGLE_BOOKS -> dbRepository.addItems(newItem)
            }
        }
    }

    override fun updateDetailStateIfNeeded(newItem: BasicLibraryElement) {
        detailState.value.takeIf { state ->
            state is LoadingStateToDetail.Data
                    && state.data.id == newItem.item.id
        }?.let { state ->
            setDetailState(
                (state as LoadingStateToDetail.Data)
                    .data.copy(description = newItem.fullInformation())
            )
        }
    }

    companion object {
        private const val TOAST_MAX_NAME_LENGTH = 30
        private const val TOAST_THREE_DOTS_LENGTH = 3

        private const val PAGE_START_POSITION = 0
        private const val INIT_TOTAL_COUNT = 0L

        private const val DROP_DEFAULT = 0
        private const val DROP_COUNT = 1

        const val LOAD_THRESHOLD = 10
        const val INIT_SIZE = 48
        const val COUNT_FOR_LOAD = INIT_SIZE / 2
        private const val DISPLAYED_PAGES = 2

        const val LOADING_STATE_DEFAULT = 0
        const val LOADING_STATE_NEXT = -1
        const val LOADING_STATE_PREV = 1
    }
}