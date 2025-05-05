package ru.phantom.library.presentation.selected_item.states

import ru.phantom.library.presentation.selected_item.DetailFragment

data class DetailState(
    val uiType: Int = DetailFragment.Companion.DEFAULT_TYPE,
    val name: String = DetailFragment.Companion.DEFAULT_NAME,
    val id: Long = DetailFragment.Companion.DEFAULT_ID,
    val image: Int = DetailFragment.Companion.DEFAULT_IMAGE,
    val description: String? = null
)