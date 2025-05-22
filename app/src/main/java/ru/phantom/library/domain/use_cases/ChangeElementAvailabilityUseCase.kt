package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository

interface ChangeElementAvailabilityUseCase {
    val repository: ItemsRepository<BasicLibraryElement>

    suspend operator fun invoke(position: Int, element: BasicLibraryElement): BasicLibraryElement
}