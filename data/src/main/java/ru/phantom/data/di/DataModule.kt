package ru.phantom.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.GoogleBooksRepository
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.data.local.dao.LibraryDB
import ru.phantom.data.local.entities.extensions.ToEntityMappers
import ru.phantom.data.local.repository.DBRepository
import ru.phantom.data.remote.retrofit.RemoteGoogleBooksRepository
import ru.phantom.data.remote.retrofit.RetrofitHelper

@Module
internal class DataModule {

    @DataScope
    @Provides
    fun provideRemoteGoogleBooksRepository(): GoogleBooksRepository {
        return RemoteGoogleBooksRepository(RetrofitHelper.createRetrofit())
    }

    @DataScope
    @Provides
    fun provideDBRepository(db: LibraryDB, toEntityMappers: ToEntityMappers): ItemsRepository<BasicLibraryElement> {
        return DBRepository(db, toEntityMappers)
    }

    @DataScope
    @Provides
    fun provideLibraryDB(
        context: Context,
        roomCallback: LibraryDB.RoomCallback
    ): LibraryDB {
        return Room.databaseBuilder(
            context,
            LibraryDB::class.java,
            "library.db"
        )
            .fallbackToDestructiveMigration(false)
            .addCallback(roomCallback)
            .build()
    }
}
