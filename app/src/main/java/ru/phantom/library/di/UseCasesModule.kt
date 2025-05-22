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
import ru.phantom.library.domain.use_cases_impls.AddItemInLibraryUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.CancelShowElementUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.ChangeDetailStateUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.ChangeElementAvailabilityUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.EmulateDelayUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.GetGoogleBooksUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.GetPaginatedLibraryItemsUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.GetTotalCountAndRemoveElementUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.SetSortTypeUseCaseImpl
import ru.phantom.library.domain.use_cases_impls.ShowDetailInformationUseCaseImpl

@Module
class UseCasesModule {

    @Provides
    fun provideAddItemInLibraryUseCase(
        repository: ItemsRepository<BasicLibraryElement>
    ) : AddItemInLibraryUseCase {
        return AddItemInLibraryUseCaseImpl(repository)
    }

    @Provides
    fun provideChangeDetailStateUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : ChangeDetailStateUseCase {
        return ChangeDetailStateUseCaseImpl(repository)
    }

    @Provides
    fun provideChangeElementAvailabilityUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : ChangeElementAvailabilityUseCase {
        return ChangeElementAvailabilityUseCaseImpl(repository)
    }

    @Provides
    fun provideEmulateDelayUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : EmulateDelayUseCase {
        return EmulateDelayUseCaseImpl(repository)
    }

    @Provides
    fun provideGetPaginatedLibraryItemsUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : GetPaginatedLibraryItemsUseCase {
        return GetPaginatedLibraryItemsUseCaseImpl(repository)
    }

    @Provides
    fun provideGetTotalCountAndRemoveElementUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : GetTotalCountAndRemoveElementUseCase {
        return GetTotalCountAndRemoveElementUseCaseImpl(repository)
    }

    @Provides
    fun provideSetSortTypeUseCase (
        repository: ItemsRepository<BasicLibraryElement>
    ) : SetSortTypeUseCase {
        return SetSortTypeUseCaseImpl(repository)
    }

    @Provides
    fun provideGetGoogleBooksUseCase (
        repository: GoogleBooksRepository
    ) : GetGoogleBooksUseCase {
        return GetGoogleBooksUseCaseImpl(repository)
    }

    @Provides
    fun provideCancelShowElementUseCase (
    ) : CancelShowElementUseCase {
        return CancelShowElementUseCaseImpl()
    }

    @Provides
    fun provideShowDetailInformationUseCase (
    ) : ShowDetailInformationUseCase {
        return ShowDetailInformationUseCaseImpl()
    }
}