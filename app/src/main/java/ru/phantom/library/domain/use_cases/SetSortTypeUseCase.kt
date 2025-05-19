package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.extensions.SetSortType
import ru.phantom.common.repository.filters.SortType
import javax.inject.Inject

class SetSortTypeUseCase @Inject constructor(
    private val repository: ItemsRepository<BasicLibraryElement>
) {
    operator fun invoke(sortType: SortType) {
        (repository as? SetSortType)?.setSortType(sortType)
    }
}