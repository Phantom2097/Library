package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.filters.SortType

interface SetSortTypeUseCase {
    val repository: ItemsRepository<BasicLibraryElement>

    operator fun invoke(sortType: SortType)
}