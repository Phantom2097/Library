package ru.phantom.data.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.GoogleBooksRepository
import ru.phantom.common.repository.ItemsRepository

@DataScope
@Component(modules = [DataModule::class, MappersModule::class])
interface DataComponent {

    val remoteRepository: GoogleBooksRepository
    val dbRepository: ItemsRepository<BasicLibraryElement>

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context
        ) : DataComponent
    }
}