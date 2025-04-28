package ru.phantom.library.presentation.main

import android.app.Application
import androidx.room.Room
import ru.phantom.library.data.dao.LibraryDB

class App : Application() {
    val database: LibraryDB by lazy {
        Room.databaseBuilder(
            applicationContext,
            LibraryDB::class.java,
            "library.db"
        ).build()
    }

    companion object {
//        val service = LibraryService
//        fun initStartItems() {
//            createBooks()
//            createNewspapers()
//            createDisks()
//        }
    }
}