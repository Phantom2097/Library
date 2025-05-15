package ru.phantom.library.domain.use_cases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository

class AddItemInLibraryUseCase (
    private val repository: ItemsRepository<BasicLibraryElement>
) {
    suspend operator fun invoke(element: BasicLibraryElement) = withContext(Dispatchers.IO) {
        repository.addItems(element)
    }
}