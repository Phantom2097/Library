package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.phantom.library.data.local.entities.ItemEntity

@Dao
interface ItemDao {
    @Insert
    suspend fun insertItem(item: ItemEntity): Long

    @Query("SELECT * FROM items")
    fun getItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items ORDER BY time")
    fun getItemsSortedByTime(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items ORDER BY time DESC")
    fun getItemsSortedByTimeDescending(): Flow<List<ItemEntity>>

//    @Query("SELECT * FROM items ORDER BY name")
//    suspend fun getItemsSortedByName(): Flow<List<ItemEntity>>
//
//    @Query("SELECT * FROM items ORDER BY id")
//    suspend fun getItemsSortedById(): Flow<List<ItemEntity>>
}