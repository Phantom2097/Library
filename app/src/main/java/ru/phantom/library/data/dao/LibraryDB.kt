package ru.phantom.library.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.phantom.library.data.local.entities.BookEntity
import ru.phantom.library.data.local.entities.DiskEntity
import ru.phantom.library.data.local.entities.ItemEntity
import ru.phantom.library.data.local.entities.NewspaperEntity

@Database(
    entities = [
        ItemEntity::class,
        BookEntity::class,
        DiskEntity::class,
        NewspaperEntity::class
    ],
    version = 1
)
abstract class LibraryDB : RoomDatabase() {
    abstract fun itemDao() : ItemDao
    abstract fun bookDao() : BookDao
}