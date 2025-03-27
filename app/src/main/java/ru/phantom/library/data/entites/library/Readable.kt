package ru.phantom.library.data.entites.library

import presentation.colors.Colors.ANSI_RED
import presentation.colors.Colors.ANSI_RESET

/*
Я не стал разделять на отдельные интерфейсы так как по заданию для каждого типа объектов должно выводиться
одинаковое меню
 */
interface Readable {
    fun readInTheReadingRoom(): String = "${ANSI_RED}Нельзя взять в зал\n$ANSI_RESET"
    fun takeToHome(): String = "${ANSI_RED}Нельзя взять домой\n$ANSI_RESET"
    fun returnInLibrary(): String
}