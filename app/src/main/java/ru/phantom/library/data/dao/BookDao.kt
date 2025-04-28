package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.phantom.library.data.local.entities.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookInfoById(id: Long): BookEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)
}