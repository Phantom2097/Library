package ru.phantom.data.di

import dagger.Module
import dagger.Provides
import ru.phantom.data.local.dao.LibraryDB
import ru.phantom.data.local.entities.extensions.ToEntityMappers

@Module
class MappersModule {

    @DataScope
    @Provides
    fun provideToEntityMappers(db: LibraryDB): ToEntityMappers {
        return ToEntityMappers(db)
    }
}