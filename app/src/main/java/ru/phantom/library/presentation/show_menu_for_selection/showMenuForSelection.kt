package presentation.show_menu_for_selection

import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW
import presentation.show_menu_for_selection.InvalidMenuType.INVALID_INDEX


fun showMenuForSelection(
    menuMessage: String,
    nextAction: (Int) -> Boolean) {
    while (true) {
        println(menuMessage)

        val num = getIndex()

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

        val exit = nextAction(num)
        if (exit) return
    }
}

private fun getIndex(): Int {
    return readlnOrNull()?.toIntOrNull() ?: INVALID_INDEX
}

private object InvalidMenuType {
    const val INVALID_INDEX = -1
}