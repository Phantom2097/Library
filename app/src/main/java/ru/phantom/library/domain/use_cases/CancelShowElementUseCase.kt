package ru.phantom.library.domain.use_cases

import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail

class CancelShowElementUseCase {
    operator fun invoke(elementId: Long, detailState: LoadingStateToDetail): Boolean {
        val detailId = (detailState as? LoadingStateToDetail.Data)?.data
        detailId?.let {
            return elementId == it.id
        }
        return false
    }
}