package ru.phantom.library.domain.use_cases

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.models.library.items.book.BookImpl
import ru.phantom.common.models.library.items.disk.DiskImpl
import ru.phantom.common.models.library.items.newspaper.NewspaperImpl
import ru.phantom.common.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.common.repository.ItemsRepository
import javax.inject.Inject

/**
 * UseCase: Изменение доступности элемента
 */
class ChangeElementAvailabilityUseCase @Inject constructor(
    private val repository: ItemsRepository<BasicLibraryElement>
) {
    suspend operator fun invoke(position: Int, element: BasicLibraryElement): BasicLibraryElement {

        val newLibraryItem = element.item.copy(availability = !element.item.availability)
        val newItem = when (element) {
            is BookImpl -> element.copy(item = newLibraryItem)
            is DiskImpl -> element.copy(item = newLibraryItem)
            is NewspaperImpl -> element.copy(item = newLibraryItem)
            is NewspaperWithMonthImpl -> element.copy(item = newLibraryItem)
            else -> throw IllegalArgumentException("Неверный тип элемента")
        }

        Log.d(
            "Availability",
            """
                prev availability = ${element.item.availability}
                new availability = ${newItem.item.availability}
            """.trimIndent()
        )

        withContext(Dispatchers.IO) {
            repository.changeItem(position, newItem)
        }
        return newItem
    }
}