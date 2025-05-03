package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.phantom.library.data.local.entities.ItemEntity

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(itemEntity: ItemEntity): Long

    @Query("UPDATE items SET availability = :availability WHERE id = :id")
    fun updateItem(id: Long, availability: Boolean)

    @Query("DELETE FROM items WHERE id = :id")
    fun deleteItemById(id: Long)

    @Query("SELECT * FROM items LIMIT :limit OFFSET :offset")
    fun getItems(limit: Int, offset: Int): List<ItemEntity>

    @Query("SELECT * FROM items ORDER BY time LIMIT :limit OFFSET :offset")
    fun getItemsSortedByTime(limit: Int, offset: Int): List<ItemEntity>

    @Query("SELECT * FROM items ORDER BY name LIMIT :limit OFFSET :offset")
    fun getItemsSortedByName(limit: Int, offset: Int): List<ItemEntity>

    @Query("SELECT COUNT(*) FROM items")
    fun getTotalCount(): Long
}