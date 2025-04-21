package ru.phantom.library.presentation.selected_item

import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE

/**
 * Состояние, которое будет использоваться для хранения информации при создании элемента
 */
data class CreateState(
    val itemType: Int = DEFAULT_IMAGE,
    val name: String? = null,
    val id: Int? = null
)
