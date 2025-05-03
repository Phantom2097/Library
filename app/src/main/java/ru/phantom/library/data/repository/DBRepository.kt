package ru.phantom.library.data.repository

import android.accounts.NetworkErrorException
import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.phantom.library.data.dao.LibraryDB
import ru.phantom.library.data.local.entities.extensions.toElement
import ru.phantom.library.data.local.entities.extensions.toEntity
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.repository.extensions.SetSortType
import ru.phantom.library.data.repository.extensions.SimulateRealRepository
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.DEFAULT_SORT
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_BY_NAME
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_BY_TIME
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_STATE_KEY
import kotlin.random.Random


class DBRepository(
    private val db: LibraryDB,
    context: Context
) : ItemsRepository<BasicLibraryElement>,
    SetSortType,
    SimulateRealRepository {

    private val sharedPref = context.getSharedPreferences(SORT_STATE_KEY, Context.MODE_PRIVATE)
    private val sortState = MutableStateFlow(sharedPref.getString(SORT_STATE_KEY, DEFAULT_SORT))

    private var errorCounter = ERROR_COUNTER_INIT

    init {
        /*
        Добавит Элементы 4 раза (всего 15 различных элементов -> всего 60)
         */
//        resetShared()
        if (isFirstRun()) {
            CoroutineScope(Dispatchers.IO).launch {
                repeat(REPEAT_ITEMS_COUNT) {
                    initStartItems().forEach { item ->
                        addItems(item)
                    }
                }
            }
            itemsAdded()
        }
    }

    private fun isFirstRun(): Boolean {
        return sharedPref.getBoolean(INIT_START_ITEMS, true)
    }

    private fun resetShared() {
        sharedPref.edit {
            putBoolean(INIT_START_ITEMS, true)
        }
    }

    private fun itemsAdded() {
        sharedPref.edit { putBoolean(INIT_START_ITEMS, false) }
    }

    override suspend fun addItems(item: BasicLibraryElement) {
        item.toEntity(db)
    }

    override suspend fun removeItem(id: Long) {
        db.itemDao().deleteItemById(id)
    }

    /**
     * Кажется не стоило настолько дотягивать состояние сортировки
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getItems(limit: Int, offset: Int): List<BasicLibraryElement> {
        return when (sortState.value) {
            DEFAULT_SORT -> db.itemDao().getItems(limit, offset)
            SORT_BY_TIME -> db.itemDao().getItemsSortedByTime(limit, offset)
            SORT_BY_NAME -> db.itemDao().getItemsSortedByName(limit, offset)
            else -> throw IllegalStateException("Неверное состояние сортировки")
        }.mapNotNull { items ->
            items.toElement(db)
        }
    }

    override suspend fun changeItem(
        position: Int,
        newItem: BasicLibraryElement
    ) {
        db.itemDao().updateItem(newItem.item.id, newItem.item.availability)
    }

    override fun getTotalCount(): Long {
        return db.itemDao().getTotalCount()
    }

    override fun setSortType(sortType: String) {
        sharedPref.edit { putString(SORT_STATE_KEY, sortType) }
        sortState.update {
            sortType
        }
    }

    override suspend fun simulateRealRepository() {
        delayEmulator()
        errorEmulator()
    }

    /**
     * Эмулирует задержку в диапазоне от 100мс до 2000мс
     */
    suspend fun delayEmulator() {
        val time = Random.nextLong(RANDOM_START, RANDOM_END)
        delay(time)
    }

    /**
     * Эмулирует состояние ошибки каждый 5ый раз
     */
    fun errorEmulator() {
        val isError = ++errorCounter % ERROR_FREQUENCY == ERROR_COUNTER_COMPARE
        if (isError) {
            errorCounter = ERROR_COUNTER_INIT
            throw NetworkErrorException("Ошибка загрузки данных, попробуйте ещё раз")
        }
    }

    private companion object {
        private const val INIT_START_ITEMS = "key for init items"

        /**
         * Количество копий каждого элемента
         */
        private const val REPEAT_ITEMS_COUNT = 4

        private const val ERROR_COUNTER_INIT = 0
        private const val ERROR_COUNTER_COMPARE = 0
        private const val ERROR_FREQUENCY = 5

        private const val RANDOM_START = 1000L
        private const val RANDOM_END = 2000L
    }
}