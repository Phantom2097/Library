package presentation.show_digitize

import presentation.show_items.getItemNumber
import presentation.show_items.showItems
import ru.phantom.common.entities.library.Showable
import ru.phantom.common.library_service.LibraryRepository.getItem

inline fun <reified LibraryType: Showable>selectItemForDigitize(getItems: () -> List<LibraryType>): LibraryType? {
    val items = getItems()
    while (true) {
        items.apply {
            showItems()

            val num = getItemNumber() ?: return null

            val item = getItem(items, num) ?: return@apply

            return item
        }
    }
}