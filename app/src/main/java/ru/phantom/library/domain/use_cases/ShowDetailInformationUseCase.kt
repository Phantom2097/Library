package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.library.presentation.selected_item.states.DetailState

interface ShowDetailInformationUseCase {
    operator fun invoke(element: BasicLibraryElement) : DetailState
}