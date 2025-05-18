package presentation.show_digitize

import ru.phantom.common.colors.Colors.ANSI_CYAN
import ru.phantom.common.colors.Colors.ANSI_RESET
import ru.phantom.common.colors.Colors.ANSI_YELLOW
import presentation.show_items.showItems
import ru.phantom.common.library_service.LibraryRepository.addItem
import ru.phantom.common.library_service.LibraryRepository.getBooksInLibrary
import ru.phantom.common.library_service.LibraryRepository.getDigitizeItems
import ru.phantom.common.library_service.LibraryRepository.getNewspapersInLibrary
import ru.phantom.common.entities.library.Showable
import ru.phantom.common.digitize.DigitizationOffice
import ru.phantom.common.digitize.DigitizationOffice.DigitizeBook
import ru.phantom.common.digitize.DigitizationOffice.DigitizeNewspaper

fun goToDigitize(itemType: Int): Boolean {
    return when (itemType) {
        1 -> {
            digitizer(
                "книгу",
                { getBooksInLibrary() },
                { book -> DigitizeBook(book) },
            )
            true
        }
        2 -> {
            digitizer (
                "газету",
                { getNewspapersInLibrary() },
                { newspaper -> DigitizeNewspaper(newspaper) },
            )
            true
        }
        3 -> {
            getDigitizeItems().showItems()
            false
        }

        4 -> true
        else -> {
            val message = buildString {
                append(ANSI_YELLOW)
                append("Неизвестная команда\n")
                append(ANSI_CYAN)
                append("\tПопробуйте ещё раз\n")
                append(ANSI_RESET)
            }

            println(message)
            false
        }
    }
}

inline fun <reified T : Showable> digitizer(
    itemName: String,
    getItems : () -> List<T>,
    digitizeItem: (T) -> DigitizationOffice,
) {
    while (true) {
        println("Выберите $itemName для оцифровки")

        val newspaper = selectItemForDigitize { getItems() } ?: return

        val dNewspaper = digitizeItem(newspaper).toDigitize()

        addItem(dNewspaper)

        println(dNewspaper)
    }
}