package presentation.show_items

import ru.phantom.library.data.repository.LibraryRepository.getBooksInLibrary
import ru.phantom.library.data.repository.LibraryRepository.getDisksInLibrary
import ru.phantom.library.data.repository.LibraryRepository.getNewspapersInLibrary
import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW
import presentation.show_items.PrintLibraryItems.PRINT_BOOKS
import presentation.show_items.PrintLibraryItems.PRINT_DISKS
import presentation.show_items.PrintLibraryItems.PRINT_NEWSPAPERS


fun printLibraryItems(itemsType: Int) {
    when (itemsType) {
        PRINT_BOOKS -> selectItem { getBooksInLibrary() }
        PRINT_NEWSPAPERS -> selectItem { getNewspapersInLibrary() }
        PRINT_DISKS -> selectItem { getDisksInLibrary() }
        else -> {
            val message = buildString {
                append(ANSI_GREEN)
                append("*Неверный номер действия*\n")
                append(ANSI_YELLOW)
                append("Попробуйте ещё раз")
                append(ANSI_RESET)
            }

            println(message)
        }
    }
}

private object PrintLibraryItems {
    const val PRINT_BOOKS = 1
    const val PRINT_NEWSPAPERS = 2
    const val PRINT_DISKS = 3
}