package ru.phantom.library.domain.use_cases_impls

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.library.domain.use_cases.AddItemInLibraryUseCase
import javax.inject.Inject

class AddItemInLibraryUseCaseImpl @Inject constructor (
    override val repository: ItemsRepository<BasicLibraryElement>
) : AddItemInLibraryUseCase {
    override suspend operator fun invoke(element: BasicLibraryElement) = withContext(Dispatchers.IO) {
        repository.addItems(element)
    }
}