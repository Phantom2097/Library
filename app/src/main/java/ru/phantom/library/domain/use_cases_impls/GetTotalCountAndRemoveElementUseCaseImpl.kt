package ru.phantom.library.domain.use_cases_impls

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.library.domain.use_cases.GetTotalCountAndRemoveElementUseCase
import javax.inject.Inject

class GetTotalCountAndRemoveElementUseCaseImpl @Inject constructor(
    override val repository: ItemsRepository<BasicLibraryElement>
) : GetTotalCountAndRemoveElementUseCase {
    override suspend operator fun invoke() : Long {
        return repository.getTotalCount()
    }

    override suspend fun withRemove(id: Long) : Long {
        repository.removeItem(id)
        return invoke()
    }
}