package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.phantom.library.data.local.entities.ItemEntity

@Dao
interface ItemDao {
    @Insert
    suspend fun insertItem(item: ItemEntity)

    @Query("SELECT * FROM itementity")
    suspend fun getItems(): List<ItemEntity>

    @Query("SELECT * FROM itementity ORDER BY name")
    suspend fun getItemsSortedByName(): List<ItemEntity>

    @Query("SELECT * FROM itementity ORDER BY id")
    suspend fun getItemsSortedById(): List<ItemEntity>
}