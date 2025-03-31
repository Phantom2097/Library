package presentation.start_console

import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW
import presentation.show_digitize.goToDigitize
import presentation.show_manager.selectShop
import presentation.show_menu_for_selection.showMenuForSelection
import presentation.start_console.SelectLibrarySelectionConsts.DIGITIZE_MENU
import presentation.start_console.SelectLibrarySelectionConsts.SHOPS_MENU
import presentation.start_console.selectLibrarySelectionConsts.DIGITIZE
import presentation.start_console.selectLibrarySelectionConsts.GO_BACK
import presentation.start_console.selectLibrarySelectionConsts.MANAGER

private typealias MethodType = (Int) -> Unit

internal fun selectLibrarySelection(method: MethodType): Boolean {
    return when (val elementType = readlnOrNull()?.toIntOrNull()) {
        null -> {
            println(ANSI_YELLOW + "Попробуйте ещё раз\n" + ANSI_RESET)
            false
        }
        // Покупка менеджером товаров в соответствующем магазине
        MANAGER -> {
            // showShops()
            val message = SHOPS_MENU
            showMenuForSelection(message, ::selectShop)
            false
        }
        //  Оцифровка предметов
        DIGITIZE -> {
            // showStartDigitize()
            val message = DIGITIZE_MENU
            showMenuForSelection(message, ::goToDigitize)
            false
        }
        GO_BACK -> true
        else -> {
            method(elementType)
            false
        }
    }
}

object SelectLibrarySelectionConsts {
    val SHOPS_MENU = """
            Введите номер магазина, в который должен пойти менеджер
                1 - Магазин книг
                2 - Газетный киоск
                3 - Магазин дисков
                4 - Выйти в главное меню
            """.trimIndent()
    val DIGITIZE_MENU = """
            Выберите что хотите оцифровать
                1 - Книги
                2 - Газеты
                3 - Показать оцифрованные предметы
                4 - Выйти в главное меню
            """.trimIndent()
}

private object selectLibrarySelectionConsts {
    const val MANAGER = 4
    const val DIGITIZE = 5
    const val GO_BACK = 6
}