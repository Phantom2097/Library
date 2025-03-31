package ru.phantom.library.data.entites.library.items

import ru.phantom.library.data.Position

data class LibraryItem (
    val name: String,
    val id: Int,
    var availability: Boolean = true,
    var position: Position = if (availability) Position.LIBRARY else Position.UNKNOWN
)  {

//    override fun getName(): String = name
//    override fun getId(): Int = id
//    override fun getAvailability(): Boolean = availability
//    override fun setAvailability() {
//        availability = !availability
//    }
}