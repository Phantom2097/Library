package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.extensions.SimulateRealRepository

class EmulateDelayUseCase(
    private val repository: ItemsRepository<BasicLibraryElement>
) {
    suspend operator fun invoke() {
        (repository as? SimulateRealRepository)?.delayLikeRealRepository()
    }
}