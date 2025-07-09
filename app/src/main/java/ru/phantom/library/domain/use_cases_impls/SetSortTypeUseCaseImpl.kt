package ru.phantom.library.domain.use_cases_impls

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.extensions.SetSortType
import ru.phantom.common.repository.filters.SortType
import ru.phantom.library.domain.use_cases.SetSortTypeUseCase
import javax.inject.Inject

class SetSortTypeUseCaseImpl @Inject constructor(
    override val repository: ItemsRepository<BasicLibraryElement>
) : SetSortTypeUseCase {
    override operator fun invoke(sortType: SortType) {
        (repository as? SetSortType)?.setSortType(sortType)
    }
}