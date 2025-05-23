package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository

class GetTotalCountAndRemoveElementUseCase(
    private val repository: ItemsRepository<BasicLibraryElement>
) {
    suspend operator fun invoke() : Long {
        return repository.getTotalCount()
    }

    suspend fun withRemove(id: Long) : Long {
        repository.removeItem(id)
        return invoke()
    }
}