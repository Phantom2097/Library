package presentation.show_actions

import ru.phantom.library.data.entites.library.Readable
import ru.phantom.library.data.entites.library.Showable

fun <T> showActions(item: T): Boolean where T : Readable, T : Showable {
    while (true) {
        showActionsList()

        val exit = selectAction(item) ?: continue
        return exit
    }
}

fun showActionsList() {
    println(
        """
            |    1 - Взять домой 
            |    2 - Читать в читальном зале 
            |    3 - Показать подробную информацию
            |    4 - Вернуть в библиотеку 
            |    5 - Показать краткую информацию
            |    6 - Вернуться к выбору 
            |    7 - Выйти в стартовое меню
    """.trimMargin()
    )
}