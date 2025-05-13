package ru.phantom.data.local.repository

import android.accounts.NetworkErrorException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.extensions.SetSortType
import ru.phantom.common.repository.extensions.SimulateRealRepository
import ru.phantom.common.repository.filters.SortType
import ru.phantom.common.repository.filters.SortType.DEFAULT_SORT
import ru.phantom.common.repository.filters.SortType.SORT_BY_NAME
import ru.phantom.common.repository.filters.SortType.SORT_BY_TIME
import ru.phantom.data.local.entities.extensions.ToEntityMappers.toEntity
import ru.phantom.data.local.entities.extensions.toElement
import kotlin.random.Random

class DBRepository() : ItemsRepository<BasicLibraryElement>,
    SetSortType,
    SimulateRealRepository {

    private val sortState = MutableStateFlow(DEFAULT_SORT)

    private val db = DataBaseProvider.getDatabase()

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
    override suspend fun getItems(limit: Int, offset: Int, orderByType: SortType): Flow<List<BasicLibraryElement>> {
        return when (sortState.value) {
            DEFAULT_SORT -> db.itemDao().getItems(limit, offset)
            SORT_BY_TIME -> db.itemDao().getItemsSortedByTime(limit, offset)
            SORT_BY_NAME -> db.itemDao().getItemsSortedByName(limit, offset)
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

    override fun setSortType(sortType: SortType) {
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