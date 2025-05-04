package ru.phantom.library.presentation.main

import android.app.Application
import android.content.Context
import ru.phantom.library.data.dao.LibraryDB

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        database = LibraryDB.getDatabase(this)
        appContext = applicationContext
    }

    companion object {
        lateinit var database: LibraryDB
            private set

        lateinit var appContext: Context
            private set
    }
}