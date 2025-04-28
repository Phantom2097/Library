package ru.phantom.library.data.repository

import android.accounts.NetworkErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement

import kotlin.random.Random

class ItemsRepositoryImpl : ItemsRepository<BasicLibraryElement> {

    private val _allItems = mutableListOf<BasicLibraryElement>()
    private var isNeedLoad = true

    private val _itemsFlow = MutableStateFlow(_allItems.toList())

    private var errorCounter = ERROR_COUNTER_INIT

    override suspend fun addItems(
        item: BasicLibraryElement
    ) {
        withContext(Dispatchers.IO) {
            _allItems.add(item)
            _itemsFlow.emit(_allItems.toList())
        }
    }

    override suspend fun removeItem(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getItems(): Flow<List<BasicLibraryElement>> {
        if (isNeedLoad) {
            delay(START_DELAY)
            isNeedLoad = false
        }
        return _itemsFlow
    }

    override suspend fun changeItem(
        position: Int,
        newItem: BasicLibraryElement
    ) {
        withContext(Dispatchers.IO) {
            _allItems[position] = newItem
            _itemsFlow.emit(_allItems.toList())
        }
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

    companion object {
        private const val ERROR_COUNTER_INIT = 0
        private const val ERROR_COUNTER_COMPARE = 0
        private const val ERROR_FREQUENCY = 5

        private const val RANDOM_START = 1000L
        private const val RANDOM_END = 2000L

        private const val START_DELAY = 2000L
    }
}