package ru.phantom.common.entities.library

import ru.phantom.common.colors.Colors.ANSI_RED
import ru.phantom.common.colors.Colors.ANSI_RESET

/**
 * Интерфейс добавляет возможность для взятия предмета в зал или домой, а также для возврата назад
 */
interface Readable {
    fun readInTheReadingRoom(): String = "${ANSI_RED}Нельзя взять в зал\n$ANSI_RESET"
    fun takeToHome(): String = "${ANSI_RED}Нельзя взять домой\n$ANSI_RESET"
    fun returnInLibrary(): String
}