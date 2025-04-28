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
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}