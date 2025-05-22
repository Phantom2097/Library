package ru.phantom.library.application

import android.app.Application
import ru.phantom.data.di.DaggerDataComponent
import ru.phantom.library.di.AppComponent
import ru.phantom.library.di.AppComponentProvider
import ru.phantom.library.di.DaggerAppComponent

class App : Application(), AppComponentProvider {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        val dataComponent = DaggerDataComponent
            .factory()
            .create(this)

        appComponent = DaggerAppComponent
            .builder()
            .dataComponent(dataComponent)
            .build()
    }

    override fun getAppComponent(): AppComponent {
        return appComponent
    }
}