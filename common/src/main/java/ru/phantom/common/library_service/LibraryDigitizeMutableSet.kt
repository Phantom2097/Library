package ru.phantom.common.library_service

import presentation.colors.Colors

class LibraryDigitizeMutableSet<E>(private val set: MutableSet<E> = mutableSetOf()) :  MutableSet<E> by set {
    override fun add(element: E): Boolean {
        return if (set.contains(element)) {
            println("${Colors.ANSI_YELLOW}\nУже была оцифрована${Colors.ANSI_RESET}")
            false
        } else {
            set.add(element)
        }
    }
}