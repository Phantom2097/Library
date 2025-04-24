package ru.phantom.library.data.entites.library.items

import ru.phantom.library.data.Position

/**
 * Хранит в себе стандартные поля для всех элементов библиотеки
 * @param name обязательное поле имени
 * @param id обязательное поле id
 * @param availability доступность
 * @param position позиция (пока используется только в консольном режиме)
 */
data class LibraryItem (
    val name: String,
    val id: Int,
    var availability: Boolean = true,
    var position: Position = if (availability) Position.LIBRARY else Position.UNKNOWN
)