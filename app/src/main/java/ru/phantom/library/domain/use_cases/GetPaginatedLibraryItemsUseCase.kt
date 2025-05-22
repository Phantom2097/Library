package ru.phantom.library.domain.use_cases

import kotlinx.coroutines.flow.Flow
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.filters.SortType
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.AdapterItems

interface GetPaginatedLibraryItemsUseCase {
    val repository: ItemsRepository<BasicLibraryElement>

    suspend operator fun invoke(limit: Int, offset: Int, sortType: SortType): Flow<List<AdapterItems>>
}