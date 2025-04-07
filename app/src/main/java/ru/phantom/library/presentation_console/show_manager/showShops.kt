package presentation.show_manager

import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW
import presentation.show_manager.InvalidSelectShop.INVALID_INDEX

@Deprecated("Заменил на дженерик функцию showMenuForSelection")
fun showShops() {
    while (true) {
        textForShowShops()
        val num = getShopNumber()
        if (num == INVALID_INDEX) {
            val message = buildString {
                append(ANSI_GREEN)
                append("*Неверное число*\n")
                append(ANSI_YELLOW)
                append("Попробуйте ещё раз\n")
                append(ANSI_RESET)
            }

            println(message)
            continue
        }
        val exit = selectShop(num)
        if (exit) return
    }
}

private fun textForShowShops() {
    println("""
        Введите номер магазина, в который должен пойти менеджер
            1 - Магазин книг
            2 - Газетный киоск
            3 - Магазин дисков
            4 - Выйти в главное меню
    """.trimIndent())
}

private fun getShopNumber(): Int {
    return readlnOrNull()?.toIntOrNull() ?: INVALID_INDEX
}

private object InvalidSelectShop {
    const val INVALID_INDEX = -1
}