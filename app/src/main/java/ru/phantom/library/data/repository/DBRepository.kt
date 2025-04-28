package ru.phantom.library.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.phantom.library.data.dao.LibraryDB
import ru.phantom.library.data.local.entities.extensions.toElement
import ru.phantom.library.data.local.entities.extensions.toEntity
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.DEFAULT_SORT
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_BY_NAME
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_BY_TIME
import ru.phantom.library.presentation.main.AllLibraryItemsList.Companion.SORT_STATE_KEY
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class DBRepository(
    private val db: LibraryDB,
    context: Context
) : ItemsRepository<BasicLibraryElement> {

    private val sharedPref = context.getSharedPreferences(SORT_STATE_KEY, Context.MODE_PRIVATE)
    private val sortState = MutableStateFlow(sharedPref.getString(SORT_STATE_KEY, DEFAULT_SORT))

    init {
        /*
        Добавит Элементы 4 раза (всего 15 различных элементов -> всего 60)
         */
        if (isFirstRun()) {
            CoroutineScope(Dispatchers.IO).launch {
                repeat(4) {
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

    private fun itemsAdded() {
        sharedPref.edit { putBoolean(INIT_START_ITEMS, false).apply() }
    }

    override suspend fun addItems(item: BasicLibraryElement) {
        item.toEntity(db)
    }

    override suspend fun removeItem(id: Long) {
        db.itemDao().deleteItemById(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getItems(): Flow<List<BasicLibraryElement>> {
        return sortState.flatMapLatest {
            when (sortState.value) {
                DEFAULT_SORT -> db.itemDao().getItems()
                SORT_BY_TIME -> db.itemDao().getItemsSortedByTime()
                SORT_BY_NAME -> db.itemDao().getItemsSortedByName()
                else -> throw IllegalStateException("Неверное состояние сортировки")
            }.map { itemEntities ->
                itemEntities.mapNotNull {
                    it.toElement(db)
                }
            }
        }
    }

    override suspend fun changeItem(
        position: Int,
        newItem: BasicLibraryElement
    ) {
        db.itemDao().updateItem(newItem.item.id, newItem.item.availability)
    }

    fun setSortType(sortType: String) {
        sortState.update {
            sharedPref.edit { putString(SORT_STATE_KEY, sortType) }
            sortType
        }
    }

    private companion object {
        private const val INIT_START_ITEMS = "key for init items"
    }
}