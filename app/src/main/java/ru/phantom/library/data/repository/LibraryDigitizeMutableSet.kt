package ru.phantom.library.data.repository

import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW

class LibraryDigitizeMutableSet<E>(private val set: MutableSet<E> = mutableSetOf()) :  MutableSet<E> by set {
    override fun add(element: E): Boolean {
        return if (set.contains(element)) {
            println("$ANSI_YELLOW\nУже была оцифрована$ANSI_RESET")
            false
        } else {
            set.add(element)
        }
    }
}