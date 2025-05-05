package ru.phantom.library.presentation.selected_item.states

import ru.phantom.library.presentation.selected_item.DetailFragment

/**
 * Состояние, которое будет использоваться для хранения информации при создании элемента
 */
data class CreateState(
    val itemType: Int = DetailFragment.Companion.DEFAULT_IMAGE,
    val name: String? = null,
    val id: Long? = null
)