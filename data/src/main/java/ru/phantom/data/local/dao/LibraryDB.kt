package ru.phantom.data.local.dao

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.phantom.data.local.dao.LibraryDB.Companion.DATABASE_VERSION
import ru.phantom.data.local.entities.BookEntity
import ru.phantom.data.local.entities.DiskEntity
import ru.phantom.data.local.entities.ItemEntity
import ru.phantom.data.local.entities.NewspaperEntity
import ru.phantom.data.local.entities.extensions.ToEntityMappers
import ru.phantom.data.local.repository.initStartItems
import javax.inject.Provider

@Database(
    entities = [
        ItemEntity::class,
        BookEntity::class,
        DiskEntity::class,
        NewspaperEntity::class
    ],
    version = DATABASE_VERSION
)
abstract class LibraryDB : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun bookDao(): BookDao
    abstract fun diskDao(): DiskDao
    abstract fun newspaperDao(): NewspaperDao
    abstract fun insertDao(): InsertDao

    internal companion object {
        private const val DATABASE_VERSION = 2

        private const val REPEAT_COUNT = 8
    }

    class RoomCallback @Inject constructor(
        private val toEntityMappersProvider: Provider<ToEntityMappers>
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                val toEntityMappers = toEntityMappersProvider.get()
                repeat(REPEAT_COUNT) {
                    initStartItems().forEach { item ->
                        toEntityMappers.toEntity(item)
                    }
                }
                Log.d("DB", "Добавлены начальные элементы в БД")
            }
        }
    }
}