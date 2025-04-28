package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.phantom.library.data.local.entities.ItemEntity

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(itemEntity: ItemEntity): Long

    @Query("UPDATE items SET availability = :availability WHERE id = :id")
    fun updateItem(id: Long, availability: Boolean)

    @Query("DELETE FROM items WHERE id = :id")
    fun deleteItemById(id: Long)

    @Query("SELECT * FROM items")
    fun getItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items ORDER BY time")
    fun getItemsSortedByTime(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items ORDER BY time DESC")
    fun getItemsSortedByTimeDescending(): Flow<List<ItemEntity>>
}