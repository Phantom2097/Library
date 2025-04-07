package presentation.show_digitize

import presentation.colors.Colors.ANSI_GREEN
import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW
import presentation.show_digitize.InvalidGetDigitizeType.INVALID_INDEX

@Deprecated("Заменил на дженерик функцию showMenuForSelection")
fun showStartDigitize() {
    while (true) {
        textForShowStartDigitize()

        val num = getDigitizeType()

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

        val exit = goToDigitize(num)
        if (exit) return
    }
}

private fun getDigitizeType(): Int {
    return readlnOrNull()?.toIntOrNull() ?: INVALID_INDEX
}

private fun textForShowStartDigitize() {
    println("""
        Выберите что хотите оцифровать
            1 - Книги
            2 - Газеты
            3 - Показать оцифрованные предметы
            4 - Выйти в главное меню
    """.trimIndent())
}

private object InvalidGetDigitizeType {
    const val INVALID_INDEX = -1
}