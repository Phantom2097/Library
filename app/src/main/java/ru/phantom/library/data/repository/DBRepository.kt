package ru.phantom.library.data.repository

import android.accounts.NetworkErrorException
import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import ru.phantom.library.data.local.entities.extensions.ToEntityMappers.toEntity
import ru.phantom.library.data.local.entities.extensions.toElement
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.repository.extensions.SetSortType
import ru.phantom.library.data.repository.extensions.SimulateRealRepository
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.DEFAULT_SORT
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_BY_NAME
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_BY_TIME
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_STATE_KEY
import ru.phantom.library.presentation.main.App
import kotlin.random.Random

class DBRepository() : ItemsRepository<BasicLibraryElement>,
    SetSortType,
    SimulateRealRepository {

    private val sharedPref = App.appContext.getSharedPreferences(SORT_STATE_KEY, Context.MODE_PRIVATE)
    private val sortState = MutableStateFlow(sharedPref.getString(SORT_STATE_KEY, DEFAULT_SORT))

    private val db = App.database

    private var errorCounter = ERROR_COUNTER_INIT

    override suspend fun addItems(item: BasicLibraryElement) {
        item.toEntity()
    }

    override suspend fun removeItem(id: Long) {
        db.itemDao().deleteItemById(id)
    }

    /**
     * Кажется не стоило настолько дотягивать состояние сортировки
     */
    override suspend fun getItems(limit: Int, offset: Int): Flow<List<BasicLibraryElement>> {
        return when (sortState.value) {
            DEFAULT_SORT -> db.itemDao().getItems(limit, offset)
            SORT_BY_TIME -> db.itemDao().getItemsSortedByTime(limit, offset)
            SORT_BY_NAME -> db.itemDao().getItemsSortedByName(limit, offset)
            else -> throw IllegalStateException("Неверное состояние сортировки")
        }.mapNotNull { listEntity ->
            listEntity.mapNotNull { element ->
                element.toElement(db)
            }
        }
    }

    override suspend fun changeItem(
        position: Int,
        newItem: BasicLibraryElement
    ) {
        db.itemDao().updateItem(newItem.item.id, newItem.item.availability)
    }

    override suspend fun getTotalCount(): Long {
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

    override suspend fun delayLikeRealRepository() {
        delayEmulator()
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
        private const val ERROR_COUNTER_INIT = 0
        private const val ERROR_COUNTER_COMPARE = 0
        private const val ERROR_FREQUENCY = 5

        private const val RANDOM_START = 1000L
        private const val RANDOM_END = 2000L
    }
}