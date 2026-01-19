package ru.phantom.library.presentation.main

import android.content.Context
import android.util.Log
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.library_service.LibraryElementFactory.createBook
import ru.phantom.common.library_service.LibraryElementFactory.createDisk
import ru.phantom.common.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.repository.filters.SortType
import ru.phantom.library.domain.use_cases.AddItemInLibraryUseCase
import ru.phantom.library.domain.use_cases.CancelShowElementUseCase
import ru.phantom.library.domain.use_cases.ChangeDetailStateUseCase
import ru.phantom.library.domain.use_cases.ChangeElementAvailabilityUseCase
import ru.phantom.library.domain.use_cases.EmulateDelayUseCase
import ru.phantom.library.domain.use_cases.GetGoogleBooksUseCase
import ru.phantom.library.domain.use_cases.GetPaginatedLibraryItemsUseCase
import ru.phantom.library.domain.use_cases.GetTotalCountAndRemoveElementUseCase
import ru.phantom.library.domain.use_cases.SetSortTypeUseCase
import ru.phantom.library.domain.use_cases.ShowDetailInformationUseCase
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.AdapterItems
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.AdapterItems.DataItem
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.AdapterItems.LoadItem
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.events.ItemClickEvent
import ru.phantom.library.presentation.main.DisplayStates.GOOGLE_BOOKS
import ru.phantom.library.presentation.main.DisplayStates.MY_LIBRARY
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.states.CreateState
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail
import java.util.concurrent.CancellationException
import javax.inject.Inject

/**
 *  Вью модель
 *  @param elements хранит список всех элементов
 *  @param detailState хранит состояние
 *  @param scrollToEnd хранит состояние положения адаптера списка,
 *  пока используется для проматывания вниз
 *  @param loadingState Отображает состояние загрузки
 *  @param requestDescription Состояние фильтров для запроса
 *  @param errorRequest Ошибки при выполнении запроса
 *
 *  @see ru.phantom.library.presentation.selected_item.DetailFragment
 *  @see DetailState
 */
class MainViewModel @Inject constructor(
    private val getPaginatedLibraryItemsUseCase: GetPaginatedLibraryItemsUseCase,
    private val changeElementAvailabilityUseCase: ChangeElementAvailabilityUseCase,
    private val addItemInLibraryUseCase: AddItemInLibraryUseCase,
    private val changeDetailStateUseCase: ChangeDetailStateUseCase,
    private val setSortTypeUseCase: SetSortTypeUseCase,
    private val getTotalCountAndRemoveElementUseCase: GetTotalCountAndRemoveElementUseCase,

    private val emulateDelayUseCase: EmulateDelayUseCase,

    private val getGoogleBooksUseCase: GetGoogleBooksUseCase,

    private val showDetailInformationUseCase: ShowDetailInformationUseCase,
    private val cancelShowElementUseCase: CancelShowElementUseCase
) :
    ViewModel(),
    ItemClickEvent {
    private val _elements = MutableStateFlow<List<AdapterItems>>(emptyList())
    val elements = _elements.asStateFlow()

    private val _screenModeState = MutableStateFlow<DisplayStates>(MY_LIBRARY)
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
            totalItems = getTotalCountAndRemoveElementUseCase()
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

    /**
     * Вызов метода получения книг из Google Books по API
     * @param query составленный запрос
     */
    fun getGoogleBooks(query: String) {
        viewModelScope.launch {
            val result = getGoogleBooksUseCase(query)

            loadingJob?.cancel()
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
        }
    }

    private var loadingJob: Job? = null

    private fun loadElements(limit: Int = INIT_SIZE, offset: Int = PAGE_START_POSITION) {
        loadingJob?.cancel()
        currentPage = offset / COUNT_FOR_LOAD
        loadingJob = viewModelScope.launch {
            try {
                getPaginatedLibraryItemsUseCase(
                    limit = limit,
                    offset = offset,
                    sortType = SortType.getEnumSortType(lastSortType)
                )
                    .collect(_elements)
            } catch (e: kotlinx.coroutines.CancellationException) {
                paginationLogger("Загрузка прерывается, заменяется offset", error = e)
            } finally {
                reloadLoadingState()
            }
        }
    }

    private fun reloadLoadingState() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(LOADING_RELOAD_TIME)
            _loadingState.update { LOADING_STATE_DEFAULT }
        }
    }

    private var loadNextJob: Job? = null
    private var loadPrevJob: Job? = null

    /**
     * Загрузка следующих элементов (добавляет шиммер и изменяет offset для потока элементов)
     */
    fun loadNext() {
        loadPrevJob?.cancel()
        if (loadNextJob != null || currentPage >= totalPages - DISPLAYED_PAGES) return

        startNextLoad()

        loadNextJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                emulateDelayUseCase()

                val offset = ++currentPage * COUNT_FOR_LOAD
                loadElements(offset = offset)
            } catch (e: CancellationException) {
                Log.i("PAGINATION", "Загрузка следующих элементов отменена", e)
                excludeLoadItem(dropLastCount = DROP_COUNT)
            } finally {
                loadNextJob = null
            }
        }
    }

    /**
     * Загрузка предыдущих элементов (добавляет шиммер и изменяет offset для потока элементов)
     */
    fun loadPrev() {
        loadNextJob?.cancel()
        if (loadPrevJob != null || currentPage <= PAGE_START_POSITION) return

        startPrevLoad()

        loadPrevJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                emulateDelayUseCase()

                val offset = --currentPage * COUNT_FOR_LOAD
                loadElements(offset = offset)
            } catch (e: kotlinx.coroutines.CancellationException) {
                Log.i("PAGINATION", "Загрузка прошлых элементов отменена", e)
                excludeLoadItem(dropCount = DROP_COUNT)
            } finally {
                loadPrevJob = null
            }
        }
    }

    /**
     * Очистка списка элементов
     */
    fun clearList() {
        _elements.update { emptyList() }
    }

    /**
     * Загрузка текущих элементов
     */
    fun loadCurrent() {
        loadElements(offset = currentPage * COUNT_FOR_LOAD)
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

    /**
     * Удаляет элемент шиммера при отмене загрузки
     */
    private fun excludeLoadItem(dropCount: Int = DROP_DEFAULT, dropLastCount: Int = DROP_DEFAULT) {
        _elements.update { items ->
            items.drop(dropCount).dropLast(dropLastCount)
        }
    }

    /**
     * Отвечает за логирование связанное с пагинацией
     */
    private fun paginationLogger(
        message: String,
        takeCount: Int? = null,
        error: Exception? = null
    ) {
        val text = """$message: ${_elements.value.size} items, takeCount $takeCount
            currentPage = $currentPage, startPage = $currentPage""".trimMargin()

        Log.d(
            "PAGINATION",
            text,
            error
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
                changeDetailStateUseCase(state)
                    .collect(_detailState)
                Log.d("uitype", "Состояние detailState $_detailState")
            } catch (e: CancellationException) {
                Log.w("DetailState", "Операция перехода отменена out flow", e)
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
        viewModelScope.launch {
            val element = when (elementType) {
                BOOK_IMAGE -> createBook(libraryItem)
                NEWSPAPER_IMAGE -> createNewspaper(libraryItem)
                DISK_IMAGE -> createDisk(libraryItem)
                else -> null
            }

            element?.let {
                addItemInLibraryUseCase(element)
                requestScrollToEnd()
            }
        }
    }

    /*
    Пока такая проверка
     */
    private var lastSortType = SortType.DEFAULT_SORT.name

    fun setSortType(sortState: String) {
        if (sortState != lastSortType) {
            val type = SortType.getEnumSortType(sortState)
            setSortTypeUseCase(type)
            loadElements()
            lastSortType = sortState
        }
    }

    override fun onItemClick(element: BasicLibraryElement) {
        val state = showDetailInformationUseCase(element)
        Log.d("uitype", "Элемент нажат $state")
        setDetailState(state)
    }

    override fun onItemLongClick(
        context: Context?,
        position: Int,
        element: BasicLibraryElement
    ) {
        viewModelScope.launch {
            when (_screenModeState.value) {
                MY_LIBRARY -> {
                    val realPosition = position + currentPage * COUNT_FOR_LOAD
                    val newItem = changeElementAvailabilityUseCase(realPosition, element)
                    updateDetailStateIfNeeded(newItem)
                    context?.let { longClickToast(newItem.briefInformation(), it) }
                }

                GOOGLE_BOOKS -> {
                    addItemInLibraryUseCase(element)
                    context?.let {
                        val message = formatedToastMessage(element)
                        longClickToast(message, context)
                    }
                }
            }
        }
    }

    private fun formatedToastMessage(element: BasicLibraryElement): String {
        var name = element.item.name
        if (name.length > TOAST_MAX_NAME_LENGTH) name = buildString {
            append(
                name.take(TOAST_MAX_NAME_LENGTH - TOAST_THREE_DOTS_LENGTH)
            )
            append("...")
        }
        val message = "Книга $name \nДобавлена в библиотеку"
        return message
    }

    private fun longClickToast(message: String, context: Context) {
        viewModelScope.launch(Dispatchers.Main) { }
        makeText(
            context,
            message,
            LENGTH_SHORT
        ).show()
    }

    override fun onItemSwiped(elementId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            totalItems = getTotalCountAndRemoveElementUseCase.withRemove(elementId)

            val isNeedCancel = cancelShowElementUseCase(elementId = elementId, _detailState.value)
            if (isNeedCancel) {
                setDetailState()
            }
        }
    }

    private fun updateDetailStateIfNeeded(newItem: BasicLibraryElement) {
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

        private const val INIT_SIZE = 48
        private const val COUNT_FOR_LOAD =
            INIT_SIZE / 2 // Должно быть кратно двум для корректной работы
        private const val DISPLAYED_PAGES = 2

        const val LOADING_STATE_DEFAULT = 0
        const val LOADING_STATE_NEXT = 1
        const val LOADING_STATE_PREV = -1
        private const val LOADING_RELOAD_TIME = 300L
    }
}