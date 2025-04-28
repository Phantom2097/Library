package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.phantom.library.data.local.entities.NewspaperEntity

@Dao
interface NewspaperDao {
    @Insert
    suspend fun insertNewspaper(newspaperEntity: NewspaperEntity)

    @Query("SELECT * FROM newspapers WHERE id = :id")
    suspend fun getNewspaperInfoById(id: Long): NewspaperEntity
}