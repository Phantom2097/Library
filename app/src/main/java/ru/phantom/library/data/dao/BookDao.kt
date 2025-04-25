package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.phantom.library.data.local.entities.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM bookentity WHERE ownerId = :id")
    suspend fun getBookInfoById(id: Int): BookEntity

    @Insert
    suspend fun insertBook(book: BookEntity)
}