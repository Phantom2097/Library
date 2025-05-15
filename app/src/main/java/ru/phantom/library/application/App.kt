package ru.phantom.library.application

import android.app.Application
import android.content.Context
import ru.phantom.data.local.repository.DataBaseProvider

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        DataBaseProvider.initialize(this)
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}