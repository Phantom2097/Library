package ru.phantom.library.domain.use_cases_impls

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.extensions.SimulateRealRepository
import ru.phantom.library.domain.use_cases.EmulateDelayUseCase
import javax.inject.Inject

class EmulateDelayUseCaseImpl @Inject constructor(
    override val repository: ItemsRepository<BasicLibraryElement>
) : EmulateDelayUseCase {
    override suspend operator fun invoke() {
        (repository as? SimulateRealRepository)?.delayLikeRealRepository()
    }
}