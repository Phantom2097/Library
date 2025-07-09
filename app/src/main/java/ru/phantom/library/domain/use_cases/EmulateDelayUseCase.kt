package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository

interface EmulateDelayUseCase {
    val repository: ItemsRepository<BasicLibraryElement>

    suspend operator fun invoke()
}