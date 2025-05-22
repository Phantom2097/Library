package ru.phantom.library.domain.use_cases_impls

import ru.phantom.library.domain.use_cases.CancelShowElementUseCase
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail
import javax.inject.Inject

class CancelShowElementUseCaseImpl @Inject constructor() : CancelShowElementUseCase {
    override operator fun invoke(elementId: Long, detailState: LoadingStateToDetail): Boolean {
        val detailId = (detailState as? LoadingStateToDetail.Data)?.data
        detailId?.let {
            return elementId == it.id
        }
        return false
    }
}