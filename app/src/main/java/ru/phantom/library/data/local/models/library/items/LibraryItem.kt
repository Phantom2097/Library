package ru.phantom.library.data.local.models.library.items

import ru.phantom.library.data.Position

/**
 * Хранит в себе стандартные поля для всех элементов библиотеки
 * @param name Обязательное поле названия предмета
 * @param id Обязательное поле id предмета
 * @param availability Доступность предмета
 * @param position Позиция (пока используется только в консольном режиме)
 */
data class LibraryItem (
    val name: String,
    val id: Long,
    var availability: Boolean = true,
    var position: Position = if (availability) Position.LIBRARY else Position.UNKNOWN
)