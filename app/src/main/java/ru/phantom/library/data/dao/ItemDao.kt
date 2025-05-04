package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Query
import ru.phantom.library.data.local.entities.ItemEntity

@Dao
interface ItemDao {

    @Query("UPDATE items SET availability = :availability WHERE id = :id")
    suspend fun updateItem(id: Long, availability: Boolean)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("SELECT * FROM items LIMIT :limit OFFSET :offset")
    suspend fun getItems(limit: Int, offset: Int): List<ItemEntity>

    @Query("SELECT * FROM items ORDER BY time LIMIT :limit OFFSET :offset")
    suspend fun getItemsSortedByTime(limit: Int, offset: Int): List<ItemEntity>

    @Query("SELECT * FROM items ORDER BY name LIMIT :limit OFFSET :offset")
    suspend fun getItemsSortedByName(limit: Int, offset: Int): List<ItemEntity>

    @Query("SELECT COUNT(*) FROM items")
    suspend fun getTotalCount(): Long
}