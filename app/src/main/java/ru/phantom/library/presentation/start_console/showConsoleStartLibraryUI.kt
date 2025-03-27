package ru.phantom.library.presentation.start_console

import presentation.show_items.printLibraryItems
import presentation.start_console.selectLibrarySelection

internal fun showConsoleStartLibraryUI() {
    while (true) {
        textForShowLibraryItems()

        val exit = selectLibrarySelection(::printLibraryItems)
        if (exit) return
    }
}

private fun textForShowLibraryItems() {
    println(
        """
            Введите число, для дальнейших действий
                1 - Посмотреть книги
                2 - Посмотреть газеты
                3 - Посмотреть диски
                4 - Управление менеджером
                5 - Кабинет оцифровки
                6 - Выход из меню библиотеки
            """.trimIndent()
    )
}