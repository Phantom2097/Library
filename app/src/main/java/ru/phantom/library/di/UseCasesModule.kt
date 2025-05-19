package ru.phantom.library.di

import dagger.Module
import dagger.Provides
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.GoogleBooksRepository
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.library.domain.use_cases.AddItemInLibraryUseCase
import ru.phantom.library.domain.use_cases.CancelShowElementUseCase
import ru.phantom.library.domain.use_cases.ChangeDetailStateUseCase
import ru.phantom.library.domain.use_cases.ChangeElementAvailabilityUseCase
import ru.phantom.library.domain.use_cases.EmulateDelayUseCase
import ru.phantom.library.domain.use_cases.GetGoogleBooksUseCase
import ru.phantom.library.domain.use_cases.GetPaginatedLibraryItemsUseCase
import ru.phantom.library.domain.use_cases.GetTotalCountAndRemoveElementUseCase
import ru.phantom.library.domain.use_cases.SetSortTypeUseCase
import ru.phantom.library.domain.use_cases.ShowDetailInformationUseCase

@Module
class UseCasesModule {

    @Provides
    fun provideAddItemInLibraryUseCase(
        repository: ItemsRepository<BasicLibraryElement>
    ) : AddItemInLibraryUseCase {
        return AddItemInLibraryUseCase(repository)
    }

    @Provides
    fun provideChangeDetailStateUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : ChangeDetailStateUseCase {
        return ChangeDetailStateUseCase(repository)
    }

    @Provides
    fun provideChangeElementAvailabilityUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : ChangeElementAvailabilityUseCase {
        return ChangeElementAvailabilityUseCase(repository)
    }

    @Provides
    fun provideEmulateDelayUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : EmulateDelayUseCase {
        return EmulateDelayUseCase(repository)
    }

    @Provides
    fun provideGetPaginatedLibraryItemsUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : GetPaginatedLibraryItemsUseCase {
        return GetPaginatedLibraryItemsUseCase(repository)
    }

    @Provides
    fun provideGetTotalCountAndRemoveElementUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : GetTotalCountAndRemoveElementUseCase {
        return GetTotalCountAndRemoveElementUseCase(repository)
    }

    @Provides
    fun provideSetSortTypeUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : SetSortTypeUseCase {
        return SetSortTypeUseCase(repository)
    }

    @Provides
    fun provideGetGoogleBooksUseCase (
        repository: GoogleBooksRepository
    ) : GetGoogleBooksUseCase {
        return GetGoogleBooksUseCase(repository)
    }

    @Provides
    fun provideCancelShowElementUseCase (
    ) : CancelShowElementUseCase {
        return CancelShowElementUseCase()
    }

    @Provides
    fun provideShowDetailInformationUseCase (
    ) : ShowDetailInformationUseCase {
        return ShowDetailInformationUseCase()
    }
}