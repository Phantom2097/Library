package ru.phantom.library.data.entites.library.items

import data.Position

data class LibraryItem (
    val name: String,
    val id: Int,
    var availability: Boolean = true,
    var position: Position = if (availability) Position.LIBRARY else Position.UNKNOWN
)