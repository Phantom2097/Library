package ru.phantom.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.phantom.data.local.entities.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookInfoById(id: Long): BookEntity
}