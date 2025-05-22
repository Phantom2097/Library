package ru.phantom.library.domain.use_cases

import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail

interface CancelShowElementUseCase {
    operator fun invoke(elementId: Long, detailState: LoadingStateToDetail) : Boolean
}