package ru.phantom.library.domain.use_cases_impls

import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.filters.SortType
import ru.phantom.library.domain.use_cases.GetPaginatedLibraryItemsUseCase
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.AdapterItems
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.AdapterItems.DataItem

class GetPaginatedLibraryItemsUseCaseImpl @Inject constructor(
    override val repository: ItemsRepository<BasicLibraryElement>
) : GetPaginatedLibraryItemsUseCase {
    override suspend operator fun invoke(limit: Int, offset: Int, sortType: SortType): Flow<List<AdapterItems>> {
        return repository.getItems(limit, offset, sortType)
            .map { list -> list.map { DataItem(it) } }
            .catch { _ ->
                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)
    }
}