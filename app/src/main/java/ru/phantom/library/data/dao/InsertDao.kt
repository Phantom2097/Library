package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import ru.phantom.library.data.local.entities.BookEntity
import ru.phantom.library.data.local.entities.DiskEntity
import ru.phantom.library.data.local.entities.ItemEntity
import ru.phantom.library.data.local.entities.NewspaperEntity

@Dao
interface InsertDao {

    @Transaction
    suspend fun insertItemBook(item: ItemEntity, book: BookEntity) {
        val id = insertItem(item)
        insertBook(book.copy(id = id))
    }

    @Transaction
    suspend fun insertItemNewspaper(item: ItemEntity, newspaper: NewspaperEntity) {
        val id = insertItem(item)
        insertNewspaper(newspaper.copy(id = id))
    }

    @Transaction
    suspend fun insertItemDisk(item: ItemEntity, disk: DiskEntity) {
        val id = insertItem(item)
        insertDisk(disk.copy(id = id))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewspaper(newspaper: NewspaperEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisk(disk: DiskEntity)
}