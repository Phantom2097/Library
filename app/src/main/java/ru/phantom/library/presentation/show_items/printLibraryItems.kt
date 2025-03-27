package presentation.show_items

import data.repository.LibraryRepository.getBooksInLibrary
import data.repository.LibraryRepository.getDisksInLibrary
import data.repository.LibraryRepository.getNewspapersInLibrary
import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW


fun printLibraryItems(itemsType: Int) {
    when (itemsType) {
        1 -> selectItem { getBooksInLibrary() }
        2 -> selectItem { getNewspapersInLibrary() }
        3 -> selectItem { getDisksInLibrary() }
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