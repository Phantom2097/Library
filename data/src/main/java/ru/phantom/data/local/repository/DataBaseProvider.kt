package ru.phantom.data.local.repository

import android.content.Context
import ru.phantom.data.local.dao.LibraryDB

object DataBaseProvider {
    private lateinit var database: LibraryDB

    fun initialize(context: Context) {
        database = LibraryDB.getDatabase(context)
    }

    fun getDatabase(): LibraryDB {
        check(::database.isInitialized)
        return database
    }
}