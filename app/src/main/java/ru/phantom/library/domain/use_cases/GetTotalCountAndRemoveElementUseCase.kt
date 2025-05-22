package ru.phantom.library.domain.use_cases

import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository

interface GetTotalCountAndRemoveElementUseCase {
    val repository: ItemsRepository<BasicLibraryElement>

    suspend operator fun invoke() : Long

    suspend fun withRemove(id: Long) : Long
}