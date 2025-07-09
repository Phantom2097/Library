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
import ru.phantom.data.local.dao.LibraryDB
import ru.phantom.data.local.entities.ItemEntity
import ru.phantom.data.local.entities.extensions.ToEntityMappers
import ru.phantom.data.local.entities.extensions.toElement
import javax.inject.Inject
import kotlin.random.Random

internal class DBRepository @Inject constructor(
    private val db : LibraryDB,
    private val toEntityMappers: ToEntityMappers
) : ItemsRepository<BasicLibraryElement>,
    SetSortType,
    SimulateRealRepository {

    private val sortState = MutableStateFlow(DEFAULT_SORT)

    private var isFirstLoad = true

    private var errorCounter = ERROR_COUNTER_INIT

    override suspend fun addItems(item: BasicLibraryElement) {
        toEntityMappers.toEntity(item)
    }

    override suspend fun removeItem(id: Long) {
        db.itemDao().deleteItemById(id)
    }

    /**
     * Кажется не стоило настолько дотягивать состояние сортировки
     */
    override suspend fun getItems(limit: Int, offset: Int, orderByType: SortType): Flow<List<BasicLibraryElement>> {
        if (isFirstLoad) delayLikeRealRepository().also { isFirstLoad = false }

        val typedGetElements = currentGetElements(limit, offset)

        return typedGetElements.mapNotNull { listEntity ->
            listEntity.mapNotNull { element ->
                element.toElement(db)
            }
        }
    }

    private fun currentGetElements(
        limit: Int,
        offset: Int
    ): Flow<List<ItemEntity>> = db.itemDao().run {
        when (sortState.value) {
            DEFAULT_SORT -> getItems(limit, offset)
            SORT_BY_TIME -> getItemsSortedByTime(limit, offset)
            SORT_BY_NAME -> getItemsSortedByName(limit, offset)
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