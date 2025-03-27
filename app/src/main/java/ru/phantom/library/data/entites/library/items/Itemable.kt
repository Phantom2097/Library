package ru.phantom.library.data.entites.library.items

interface Itemable {
    fun getName(): String
    fun getId(): Int
    fun getAvailability(): Boolean
    fun setAvailability()
}