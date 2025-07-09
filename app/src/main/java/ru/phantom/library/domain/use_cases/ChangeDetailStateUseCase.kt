package ru.phantom.library.domain.use_cases

import kotlinx.coroutines.flow.Flow
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail

interface ChangeDetailStateUseCase {
    val repository: ItemsRepository<BasicLibraryElement>

    operator fun invoke(state: DetailState = DetailState()) : Flow<LoadingStateToDetail>
}