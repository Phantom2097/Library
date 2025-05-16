package ru.phantom.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.phantom.data.local.entities.NewspaperEntity

@Dao
interface NewspaperDao {
    @Query("SELECT * FROM newspapers WHERE id = :id")
    suspend fun getNewspaperInfoById(id: Long): NewspaperEntity
}