package ru.phantom.library.presentation.selected_item

import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_ID
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_NAME
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_TYPE

data class DetailState(
    val uiType: Int = DEFAULT_TYPE,
    val name: String = DEFAULT_NAME,
    val id: Int = DEFAULT_ID,
    val image: Int = DEFAULT_IMAGE,
    val description: String? = null
)
