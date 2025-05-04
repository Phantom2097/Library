package ru.phantom.library.data.dao

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.phantom.library.data.dao.LibraryDB.Companion.DATABASE_VERSION
import ru.phantom.library.data.local.entities.BookEntity
import ru.phantom.library.data.local.entities.DiskEntity
import ru.phantom.library.data.local.entities.ItemEntity
import ru.phantom.library.data.local.entities.NewspaperEntity
import ru.phantom.library.data.local.entities.extensions.ToEntityMappers.toEntity
import ru.phantom.library.data.repository.initStartItems

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
    abstract fun itemDao() : ItemDao
    abstract fun bookDao() : BookDao
    abstract fun diskDao() : DiskDao
    abstract fun newspaperDao() : NewspaperDao
    abstract fun insertDao() : InsertDao

    companion object {
        private const val DATABASE_VERSION = 1

        private const val REPEAT_COUNT = 8

        @Volatile
        private var Instance: LibraryDB? = null

        val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                CoroutineScope(Dispatchers.IO).launch {
                    repeat(REPEAT_COUNT) {
                        initStartItems().forEach { item ->
                            item.toEntity()
                        }
                    }

                    Log.d("DB", "Добавлены начальные элементы в БД")
                }
            }
        }

        fun getDatabase(context: Context): LibraryDB {
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(
                    context,
                    LibraryDB::class.java,
                    "library.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .addCallback(roomCallback)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}