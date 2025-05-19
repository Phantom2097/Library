package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.entities.library.disk.Disk
import ru.phantom.common.entities.library.newspaper.Newspaper
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.SHOW_TYPE
import ru.phantom.library.presentation.selected_item.states.DetailState
import javax.inject.Inject

class ShowDetailInformationUseCase @Inject constructor() {
    operator fun invoke(element: BasicLibraryElement) : DetailState {
        val image = when (element) {
                is Book -> BOOK_IMAGE
                is Newspaper -> NEWSPAPER_IMAGE
                is Disk -> DISK_IMAGE
                else -> DEFAULT_IMAGE
            }

        val state = DetailState(
            uiType = SHOW_TYPE,
            name = element.item.name,
            id = element.item.id,
            image = image,
            description = element.fullInformation()
        )

        return state
    }
}